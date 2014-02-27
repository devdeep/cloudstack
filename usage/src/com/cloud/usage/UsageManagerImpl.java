// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// the License.  You may obtain a copy of the License at
//
// http://www.apache.org/licenses/LICENSE-2.0
//
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
package com.cloud.usage;

import java.net.InetAddress;
import java.sql.SQLException;
import java.util.ArrayList;
import java.util.Calendar;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.TimeZone;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.ScheduledExecutorService;
import java.util.concurrent.TimeUnit;

import javax.ejb.Local;
import javax.inject.Inject;
import javax.naming.ConfigurationException;

import com.cloud.event.UsageEventDetailsVO;
import com.cloud.service.ServiceOfferingVO;
import org.apache.log4j.Logger;
import org.apache.cloudstack.framework.config.dao.ConfigurationDao;
import org.apache.cloudstack.managed.context.ManagedContext;
import org.apache.cloudstack.managed.context.ManagedContextRunnable;
import org.apache.cloudstack.usage.UsageTypes;
import org.springframework.stereotype.Component;

import com.cloud.alert.AlertManager;
import com.cloud.event.EventTypes;
import com.cloud.event.UsageEventVO;
import com.cloud.event.dao.UsageEventDao;
import com.cloud.usage.dao.UsageDao;
import com.cloud.usage.dao.UsageIPAddressDao;
import com.cloud.usage.dao.UsageJobDao;
import com.cloud.usage.dao.UsageLoadBalancerPolicyDao;
import com.cloud.usage.dao.UsageNetworkDao;
import com.cloud.usage.dao.UsageNetworkOfferingDao;
import com.cloud.usage.dao.UsagePortForwardingRuleDao;
import com.cloud.usage.dao.UsageSecurityGroupDao;
import com.cloud.usage.dao.UsageStorageDao;
import com.cloud.usage.dao.UsageVMInstanceDao;
import com.cloud.usage.dao.UsageVMSnapshotDao;
import com.cloud.usage.dao.UsageVPNUserDao;
import com.cloud.usage.dao.UsageVmDiskDao;
import com.cloud.usage.dao.UsageVolumeDao;
import com.cloud.usage.parser.IPAddressUsageParser;
import com.cloud.usage.parser.LoadBalancerUsageParser;
import com.cloud.usage.parser.NetworkOfferingUsageParser;
import com.cloud.usage.parser.NetworkUsageParser;
import com.cloud.usage.parser.PortForwardingUsageParser;
import com.cloud.usage.parser.SecurityGroupUsageParser;
import com.cloud.usage.parser.StorageUsageParser;
import com.cloud.usage.parser.VMInstanceUsageParser;
import com.cloud.usage.parser.VMSnapshotUsageParser;
import com.cloud.usage.parser.VPNUserUsageParser;
import com.cloud.usage.parser.VmDiskUsageParser;
import com.cloud.usage.parser.VolumeUsageParser;
import com.cloud.user.Account;
import com.cloud.user.AccountVO;
import com.cloud.user.UserStatisticsVO;
import com.cloud.user.VmDiskStatisticsVO;
import com.cloud.user.dao.AccountDao;
import com.cloud.user.dao.UserStatisticsDao;
import com.cloud.user.dao.VmDiskStatisticsDao;
import com.cloud.utils.component.ManagerBase;
import com.cloud.utils.concurrency.NamedThreadFactory;
import com.cloud.utils.db.DB;
import com.cloud.utils.db.Filter;
import com.cloud.utils.db.GlobalLock;
import com.cloud.utils.db.SearchCriteria;
import com.cloud.utils.db.TransactionLegacy;
import com.cloud.event.dao.UsageEventDetailsDao;

@Component
@Local(value={UsageManager.class})
public class UsageManagerImpl extends ManagerBase implements UsageManager, Runnable {
    public static final Logger s_logger = Logger.getLogger(UsageManagerImpl.class.getName());

    protected static final String DAILY = "DAILY";
    protected static final String WEEKLY = "WEEKLY";
    protected static final String MONTHLY = "MONTHLY";

    private static final int HOURLY_TIME = 60;
    private static final int DAILY_TIME = 60 * 24;
    private static final int THREE_DAYS_IN_MINUTES = 60 * 24 * 3;
    private static final int USAGE_AGGREGATION_RANGE_MIN = 10;

    @Inject private AccountDao m_accountDao;
    @Inject private UserStatisticsDao m_userStatsDao;
    @Inject private UsageDao m_usageDao;
    @Inject private UsageVMInstanceDao m_usageInstanceDao;
    @Inject private UsageIPAddressDao m_usageIPAddressDao;
    @Inject private UsageNetworkDao m_usageNetworkDao;
    @Inject private UsageVolumeDao m_usageVolumeDao;
    @Inject private UsageStorageDao m_usageStorageDao;
    @Inject private UsageLoadBalancerPolicyDao m_usageLoadBalancerPolicyDao;
    @Inject private UsagePortForwardingRuleDao m_usagePortForwardingRuleDao;
    @Inject private UsageNetworkOfferingDao m_usageNetworkOfferingDao;
    @Inject private UsageVPNUserDao m_usageVPNUserDao;
    @Inject private UsageSecurityGroupDao m_usageSecurityGroupDao;
    @Inject private UsageJobDao m_usageJobDao;
    @Inject private VmDiskStatisticsDao m_vmDiskStatsDao;
    @Inject private UsageVmDiskDao m_usageVmDiskDao;
    @Inject protected AlertManager _alertMgr;
    @Inject protected UsageEventDao _usageEventDao;
    @Inject protected UsageEventDetailsDao _usageEventDetailsDao;
    @Inject ConfigurationDao _configDao;
    @Inject private UsageVMSnapshotDao m_usageVMSnapshotDao;
    
    private String m_version = null;
    private final Calendar m_jobExecTime = Calendar.getInstance();
    private int m_aggregationDuration = 0;
    private int m_sanityCheckInterval = 0;
    String m_hostname = null;
    int m_pid = 0;
    TimeZone m_usageTimezone = TimeZone.getTimeZone("GMT");;
    private final GlobalLock m_heartbeatLock = GlobalLock.getInternLock("usage.job.heartbeat.check");
    private List<UsageNetworkVO> usageNetworks = new ArrayList<UsageNetworkVO>();
    private List<UsageVmDiskVO> usageVmDisks = new ArrayList<UsageVmDiskVO>();

    private final ScheduledExecutorService m_executor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Usage-Job"));
    private final ScheduledExecutorService m_heartbeatExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Usage-HB"));
    private final ScheduledExecutorService m_sanityExecutor = Executors.newSingleThreadScheduledExecutor(new NamedThreadFactory("Usage-Sanity"));
    private Future m_scheduledFuture = null;
    private Future m_heartbeat = null;
    private Future m_sanity = null;

    public UsageManagerImpl() {
    }

    private void mergeConfigs(Map<String, String> dbParams, Map<String, Object> xmlParams) {
        for (Map.Entry<String, Object> param : xmlParams.entrySet()) {
            dbParams.put(param.getKey(), (String)param.getValue());
        }
    }

    public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
        final String run = "usage.vmops.pid";

        if (s_logger.isDebugEnabled()) {
            s_logger.debug("Checking to see if " + run + " exists.");
        }

        final Class<?> c = UsageServer.class;
        m_version = c.getPackage().getImplementationVersion();
        if (m_version == null) {
            // TODO
        	// throw new CloudRuntimeException("Unable to find the implementation version of this usage server");
        }

        if (s_logger.isInfoEnabled()) {
            s_logger.info("Implementation Version is " + m_version);
        }

        Map<String, String> configs = _configDao.getConfiguration(params);

        if (params != null) {
            mergeConfigs(configs, params);
        }

        String execTime = configs.get("usage.stats.job.exec.time");
        String aggregationRange  = configs.get("usage.stats.job.aggregation.range");
        String execTimeZone = configs.get("usage.execution.timezone");
        String aggreagationTimeZone = configs.get("usage.aggregation.timezone");
        String sanityCheckInterval = configs.get("usage.sanity.check.interval");
        if(sanityCheckInterval != null){
            m_sanityCheckInterval = Integer.parseInt(sanityCheckInterval);
        }

        if(aggreagationTimeZone != null && !aggreagationTimeZone.isEmpty()){  
            m_usageTimezone = TimeZone.getTimeZone(aggreagationTimeZone);
        }
        s_logger.debug("Usage stats aggregation time zone: "+aggreagationTimeZone);
        
        try {
            if ((execTime == null) || (aggregationRange == null)) {
                s_logger.error("missing configuration values for usage job, usage.stats.job.exec.time = " + execTime + ", usage.stats.job.aggregation.range = " + aggregationRange);
                throw new ConfigurationException("Missing configuration values for usage job, usage.stats.job.exec.time = " + execTime + ", usage.stats.job.aggregation.range = " + aggregationRange);
            }
            String[] execTimeSegments = execTime.split(":");
            if (execTimeSegments.length != 2) {
                s_logger.error("Unable to parse usage.stats.job.exec.time");
                throw new ConfigurationException("Unable to parse usage.stats.job.exec.time '" + execTime + "'");
            }
            int hourOfDay = Integer.parseInt(execTimeSegments[0]);
            int minutes = Integer.parseInt(execTimeSegments[1]);
            m_jobExecTime.setTime(new Date());

            m_jobExecTime.set(Calendar.HOUR_OF_DAY, hourOfDay);
            m_jobExecTime.set(Calendar.MINUTE, minutes);
            m_jobExecTime.set(Calendar.SECOND, 0);
            m_jobExecTime.set(Calendar.MILLISECOND, 0);
            if(execTimeZone != null && !execTimeZone.isEmpty()){
                m_jobExecTime.setTimeZone(TimeZone.getTimeZone(execTimeZone));
            }

            // if the hour to execute the job has already passed, roll the day forward to the next day
            Date execDate = m_jobExecTime.getTime();
            if (execDate.before(new Date())) {
                m_jobExecTime.roll(Calendar.DAY_OF_YEAR, true);
            }
            
            s_logger.debug("Execution Time: "+execDate.toString());
            Date currentDate = new Date(System.currentTimeMillis());
            s_logger.debug("Current Time: "+currentDate.toString());

            m_aggregationDuration = Integer.parseInt(aggregationRange);
            if (m_aggregationDuration < USAGE_AGGREGATION_RANGE_MIN) {
                s_logger.warn("Usage stats job aggregation range is to small, using the minimum value of " + USAGE_AGGREGATION_RANGE_MIN);
                m_aggregationDuration = USAGE_AGGREGATION_RANGE_MIN;
            }
            m_hostname = InetAddress.getLocalHost().getHostName() + "/" + InetAddress.getLocalHost().getHostAddress();
        } catch (NumberFormatException ex) {
            throw new ConfigurationException("Unable to parse usage.stats.job.exec.time '" + execTime + "' or usage.stats.job.aggregation.range '" + aggregationRange + "', please check configuration values");
        } catch (Exception e) {
            s_logger.error("Unhandled exception configuring UsageManger", e);
            throw new ConfigurationException("Unhandled exception configuring UsageManager " + e.toString());
        }
        m_pid = Integer.parseInt(System.getProperty("pid"));
        return true;
    }

    public boolean start() {
        if (s_logger.isInfoEnabled()) {
            s_logger.info("Starting Usage Manager");
        }

        // use the configured exec time and aggregation duration for scheduling the job
        m_scheduledFuture = m_executor.scheduleAtFixedRate(this, m_jobExecTime.getTimeInMillis()  - System.currentTimeMillis(), m_aggregationDuration * 60 * 1000, TimeUnit.MILLISECONDS);

        m_heartbeat = m_heartbeatExecutor.scheduleAtFixedRate(new Heartbeat(), /* start in 15 seconds...*/15*1000, /* check database every minute*/60*1000, TimeUnit.MILLISECONDS);
        
        if(m_sanityCheckInterval > 0){
            m_sanity = m_sanityExecutor.scheduleAtFixedRate(new SanityCheck(), 1, m_sanityCheckInterval, TimeUnit.DAYS);
        }

        TransactionLegacy usageTxn = TransactionLegacy.open(TransactionLegacy.USAGE_DB);
        try {
            if(m_heartbeatLock.lock(3)) { // 3 second timeout
                try {
                    UsageJobVO job = m_usageJobDao.getLastJob();
                    if (job == null) {
                        m_usageJobDao.createNewJob(m_hostname, m_pid, UsageJobVO.JOB_TYPE_RECURRING);
                    }
                } finally {
                    m_heartbeatLock.unlock();
                }
            } else {
                if(s_logger.isTraceEnabled())
                    s_logger.trace("Heartbeat lock is in use by others, returning true as someone else will take over the job if required");
            }
        } finally {
            usageTxn.close();
        }

        return true;
    }

    public boolean stop() {
        m_heartbeat.cancel(true);
        m_scheduledFuture.cancel(true);
        if(m_sanity != null){
            m_sanity.cancel(true);
        }
        return true;
    }

    @Override
    public void run() {
        (new ManagedContextRunnable() {
            @Override
            protected void runInContext() {
                runInContextInternal();
            }
        }).run();
    }
    
    protected void runInContextInternal() {
        if (s_logger.isInfoEnabled()) {
            s_logger.info("starting usage job...");
        }

        // how about we update the job exec time when the job starts???
        long execTime = m_jobExecTime.getTimeInMillis();
        long now = System.currentTimeMillis() + 2000; // 2 second buffer since jobs can run a little early (though usually just by milliseconds)

        if (execTime < now) {
            // if exec time is in the past, calculate the next time the job will execute...if this is a one-off job that is a result
            // of scheduleParse() then don't update the next exec time...
            m_jobExecTime.add(Calendar.MINUTE, m_aggregationDuration);
        }

        UsageJobVO job = m_usageJobDao.isOwner(m_hostname, m_pid);
        if (job != null) {
            // FIXME: we really need to do a better job of not missing any events...so we should some how
            //        keep track of the last time usage was run, then go from there...
            // For executing the job, we treat hourly and daily as special time ranges, using the previous full hour or the previous
            // full day.  Otherwise we just subtract off the aggregation range from the current time and use that as start date with
            // current time as end date.
            Calendar cal = Calendar.getInstance(m_usageTimezone);
            cal.setTime(new Date());
            long startDate = 0;
            long endDate = 0;
            if (m_aggregationDuration == DAILY_TIME) {
                cal.roll(Calendar.DAY_OF_YEAR, false);
                cal.set(Calendar.HOUR_OF_DAY, 0);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startDate = cal.getTime().getTime();

                cal.roll(Calendar.DAY_OF_YEAR, true);
                cal.add(Calendar.MILLISECOND, -1);
                endDate = cal.getTime().getTime();
            } else if (m_aggregationDuration == HOURLY_TIME) {
                cal.roll(Calendar.HOUR_OF_DAY, false);
                cal.set(Calendar.MINUTE, 0);
                cal.set(Calendar.SECOND, 0);
                cal.set(Calendar.MILLISECOND, 0);
                startDate = cal.getTime().getTime();

                cal.roll(Calendar.HOUR_OF_DAY, true);
                cal.add(Calendar.MILLISECOND, -1);
                endDate = cal.getTime().getTime();
            } else {
                endDate = cal.getTime().getTime(); // current time
                cal.add(Calendar.MINUTE, -1*m_aggregationDuration);
                startDate = cal.getTime().getTime();
            }

            parse(job, startDate, endDate);
        } else {
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("Not owner of usage job, skipping...");
            }
        }
        if (s_logger.isInfoEnabled()) {
            s_logger.info("usage job complete");
        }
    }

    public void scheduleParse() {
        if (s_logger.isDebugEnabled()) {
            s_logger.debug("Scheduling Usage job...");
        }
        m_executor.schedule(this, 0, TimeUnit.MILLISECONDS);
    }

    public void parse(UsageJobVO job, long startDateMillis, long endDateMillis) {
        // TODO: Shouldn't we also allow parsing by the type of usage?

        boolean success = false;
        long timeStart = System.currentTimeMillis();
        long deleteOldStatsTimeMillis = 0L;
        try {
            if ((endDateMillis == 0) || (endDateMillis > timeStart)) {
                endDateMillis = timeStart;
            }

            long lastSuccess = m_usageJobDao.getLastJobSuccessDateMillis();
            if (lastSuccess != 0) {
                startDateMillis = lastSuccess+1; // 1 millisecond after
            }

            if (startDateMillis >= endDateMillis) {
                if (s_logger.isInfoEnabled()) {
                    s_logger.info("not parsing usage records since start time mills (" + startDateMillis + ") is on or after end time millis (" + endDateMillis + ")");
                }

                TransactionLegacy jobUpdateTxn = TransactionLegacy.open(TransactionLegacy.USAGE_DB);
                try {
                    jobUpdateTxn.start();
                    // everything seemed to work...set endDate as the last success date
                    m_usageJobDao.updateJobSuccess(job.getId(), startDateMillis, endDateMillis, System.currentTimeMillis() - timeStart, success);

                    // create a new job if this is a recurring job
                    if (job.getJobType() == UsageJobVO.JOB_TYPE_RECURRING) {
                        m_usageJobDao.createNewJob(m_hostname, m_pid, UsageJobVO.JOB_TYPE_RECURRING);
                    }
                    jobUpdateTxn.commit();
                } finally {
                    jobUpdateTxn.close();
                }

                return;
            }
            deleteOldStatsTimeMillis = startDateMillis;

            Date startDate = new Date(startDateMillis);
            Date endDate = new Date(endDateMillis);
            if (s_logger.isInfoEnabled()) {
                s_logger.info("Parsing usage records between " + startDate + " and " + endDate);
            }

            List<AccountVO> accounts = null;
            List<UserStatisticsVO> userStats = null;
            Map<String, UsageNetworkVO> networkStats = null;
            List<VmDiskStatisticsVO> vmDiskStats = null;
            Map<String, UsageVmDiskVO> vmDiskUsages = null;
            TransactionLegacy userTxn = TransactionLegacy.open(TransactionLegacy.CLOUD_DB);
            try {
                Long limit = Long.valueOf(500);
                Long offset = Long.valueOf(0);
                Long lastAccountId = m_usageDao.getLastAccountId();
                if (lastAccountId == null) {
                    lastAccountId = Long.valueOf(0);
                }

                do {
                    Filter filter = new Filter(AccountVO.class, "id", true, offset, limit);

                    accounts = m_accountDao.findActiveAccounts(lastAccountId, filter);

                    if ((accounts != null) && !accounts.isEmpty()) {
                        // now update the accounts in the cloud_usage db
                        m_usageDao.updateAccounts(accounts);
                    }
                    offset = new Long(offset.longValue() + limit.longValue());
                } while ((accounts != null) && !accounts.isEmpty());

                // reset offset
                offset = Long.valueOf(0);

                do {
                    Filter filter = new Filter(AccountVO.class, "id", true, offset, limit);

                    accounts = m_accountDao.findRecentlyDeletedAccounts(lastAccountId, startDate, filter);

                    if ((accounts != null) && !accounts.isEmpty()) {
                        // now update the accounts in the cloud_usage db
                        m_usageDao.updateAccounts(accounts);
                    }
                    offset = new Long(offset.longValue() + limit.longValue());
                } while ((accounts != null) && !accounts.isEmpty());

                // reset offset
                offset = Long.valueOf(0);

                do {
                    Filter filter = new Filter(AccountVO.class, "id", true, offset, limit);

                    accounts = m_accountDao.findNewAccounts(lastAccountId, filter);

                    if ((accounts != null) && !accounts.isEmpty()) {
                        // now copy the accounts to cloud_usage db
                        m_usageDao.saveAccounts(accounts);
                    }
                    offset = new Long(offset.longValue() + limit.longValue());
                } while ((accounts != null) && !accounts.isEmpty());

                // reset offset
                offset = Long.valueOf(0);

                // get all the user stats to create usage records for the network usage
                Long lastUserStatsId = m_usageDao.getLastUserStatsId();
                if (lastUserStatsId == null) {
                    lastUserStatsId = Long.valueOf(0);
                }

                SearchCriteria<UserStatisticsVO> sc2 = m_userStatsDao.createSearchCriteria();
                sc2.addAnd("id", SearchCriteria.Op.LTEQ, lastUserStatsId);
                do {
                    Filter filter = new Filter(UserStatisticsVO.class, "id", true, offset, limit);

                    userStats = m_userStatsDao.search(sc2, filter);

                    if ((userStats != null) && !userStats.isEmpty()) {
                        // now copy the accounts to cloud_usage db
                        m_usageDao.updateUserStats(userStats);
                    }
                    offset = new Long(offset.longValue() + limit.longValue());
                } while ((userStats != null) && !userStats.isEmpty());

                // reset offset
                offset = Long.valueOf(0);

                sc2 = m_userStatsDao.createSearchCriteria();
                sc2.addAnd("id", SearchCriteria.Op.GT, lastUserStatsId);
                do {
                    Filter filter = new Filter(UserStatisticsVO.class, "id", true, offset, limit);

                    userStats = m_userStatsDao.search(sc2, filter);

                    if ((userStats != null) && !userStats.isEmpty()) {
                        // now copy the accounts to cloud_usage db
                        m_usageDao.saveUserStats(userStats);
                    }
                    offset = new Long(offset.longValue() + limit.longValue());
                } while ((userStats != null) && !userStats.isEmpty());

                // reset offset
                offset = Long.valueOf(0);

                // get all the vm network stats to create usage_vm_network records for the vm network usage
                Long lastVmDiskStatsId = m_usageDao.getLastVmDiskStatsId();
                if (lastVmDiskStatsId == null) {
                       lastVmDiskStatsId = Long.valueOf(0);
                }
                SearchCriteria<VmDiskStatisticsVO> sc4 = m_vmDiskStatsDao.createSearchCriteria();
                sc4.addAnd("id", SearchCriteria.Op.LTEQ, lastVmDiskStatsId);
                do {
                    Filter filter = new Filter(VmDiskStatisticsVO.class, "id", true, offset, limit);

                    vmDiskStats = m_vmDiskStatsDao.search(sc4, filter);

                    if ((vmDiskStats != null) && !vmDiskStats.isEmpty()) {
                        // now copy the accounts to cloud_usage db
                        m_usageDao.updateVmDiskStats(vmDiskStats);
                    }
                    offset = new Long(offset.longValue() + limit.longValue());
                } while ((vmDiskStats != null) && !vmDiskStats.isEmpty());

                // reset offset
                offset = Long.valueOf(0);

                sc4 = m_vmDiskStatsDao.createSearchCriteria();
                sc4.addAnd("id", SearchCriteria.Op.GT, lastVmDiskStatsId);
                do {
                    Filter filter = new Filter(VmDiskStatisticsVO.class, "id", true, offset, limit);

                    vmDiskStats = m_vmDiskStatsDao.search(sc4, filter);

                    if ((vmDiskStats != null) && !vmDiskStats.isEmpty()) {
                        // now copy the accounts to cloud_usage db
                        m_usageDao.saveVmDiskStats(vmDiskStats);
                    }
                    offset = new Long(offset.longValue() + limit.longValue());
                } while ((vmDiskStats != null) && !vmDiskStats.isEmpty());

            } finally {
                userTxn.close();
            }

            // TODO:  Fetch a maximum number of events and process them before moving on to the next range of events

            // - get a list of the latest events
            // - insert the latest events into the usage.events table
            List<UsageEventVO> events = _usageEventDao.getRecentEvents(new Date(endDateMillis));

            
            TransactionLegacy usageTxn = TransactionLegacy.open(TransactionLegacy.USAGE_DB);
            try {
                usageTxn.start();

                // make sure start date is before all of our un-processed events (the events are ordered oldest
                // to newest, so just test against the first event)
                if ((events != null) && (events.size() > 0)) {
                    Date oldestEventDate = events.get(0).getCreateDate();
                    if (oldestEventDate.getTime() < startDateMillis) {
                        startDateMillis = oldestEventDate.getTime();
                        startDate = new Date(startDateMillis);
                    }

                    // - loop over the list of events and create entries in the helper tables
                    // - create the usage records using the parse methods below
                    for (UsageEventVO event : events) {
                        event.setProcessed(true);
                        _usageEventDao.update(event.getId(), event);
                        createHelperRecord(event);
                    }
                }

                // TODO:  Fetch a maximum number of user stats and process them before moving on to the next range of user stats

                // get user stats in order to compute network usage
                networkStats = m_usageNetworkDao.getRecentNetworkStats();

                Calendar recentlyDeletedCal = Calendar.getInstance(m_usageTimezone);
                recentlyDeletedCal.setTimeInMillis(startDateMillis);
                recentlyDeletedCal.add(Calendar.MINUTE, -1*THREE_DAYS_IN_MINUTES);
                Date recentlyDeletedDate = recentlyDeletedCal.getTime();

                // Keep track of user stats for an account, across all of its public IPs
                Map<String, UserStatisticsVO> aggregatedStats = new HashMap<String, UserStatisticsVO>();
                int startIndex = 0;
                do {                    
                    userStats = m_userStatsDao.listActiveAndRecentlyDeleted(recentlyDeletedDate, startIndex, 500);
                    
                    if (userStats != null) {                        
                        for (UserStatisticsVO userStat : userStats) {
                            if(userStat.getDeviceId() != null){
                                String hostKey = userStat.getDataCenterId() + "-" + userStat.getAccountId()+"-Host-" + userStat.getDeviceId();
                                UserStatisticsVO hostAggregatedStat = aggregatedStats.get(hostKey);
                                if (hostAggregatedStat == null) {
                                    hostAggregatedStat = new UserStatisticsVO(userStat.getAccountId(), userStat.getDataCenterId(), userStat.getPublicIpAddress(), 
                                            userStat.getDeviceId(), userStat.getDeviceType(), userStat.getNetworkId());
                                }
                                
                                hostAggregatedStat.setAggBytesSent(hostAggregatedStat.getAggBytesSent() + userStat.getAggBytesSent());
                                hostAggregatedStat.setAggBytesReceived(hostAggregatedStat.getAggBytesReceived() + userStat.getAggBytesReceived());
                                aggregatedStats.put(hostKey, hostAggregatedStat);
                            }
                        }                                                
                    }
                    startIndex += 500;
                } while ((userStats != null) && !userStats.isEmpty());

                // loop over the user stats, create delta entries in the usage_network helper table
                int numAcctsProcessed = 0;
                usageNetworks.clear();
                for (String key : aggregatedStats.keySet()) {
                    UsageNetworkVO currentNetworkStats = null;
                    if (networkStats != null) {
                        currentNetworkStats = networkStats.get(key);
                    }

                    createNetworkHelperEntry(aggregatedStats.get(key), currentNetworkStats, endDateMillis);
                    numAcctsProcessed++;
                }
                m_usageNetworkDao.saveUsageNetworks(usageNetworks);

                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("created network stats helper entries for " + numAcctsProcessed + " accts");
                }

                // get vm disk stats in order to compute vm disk usage
                vmDiskUsages = m_usageVmDiskDao.getRecentVmDiskStats();

                // Keep track of user stats for an account, across all of its public IPs
                Map<String, VmDiskStatisticsVO> aggregatedDiskStats = new HashMap<String, VmDiskStatisticsVO>();
                startIndex = 0;
                do {
                       vmDiskStats = m_vmDiskStatsDao.listActiveAndRecentlyDeleted(recentlyDeletedDate, startIndex, 500);

                    if (vmDiskUsages != null) {
                        for (VmDiskStatisticsVO vmDiskStat : vmDiskStats) {
                            if(vmDiskStat.getVmId() != null){
                                String hostKey = vmDiskStat.getDataCenterId() + "-" + vmDiskStat.getAccountId()+"-Vm-" + vmDiskStat.getVmId()+"-Disk-" + vmDiskStat.getVolumeId();
                                VmDiskStatisticsVO hostAggregatedStat = aggregatedDiskStats.get(hostKey);
                                if (hostAggregatedStat == null) {
                                    hostAggregatedStat = new VmDiskStatisticsVO(vmDiskStat.getAccountId(), vmDiskStat.getDataCenterId(), vmDiskStat.getVmId(),vmDiskStat.getVolumeId());
                                }

                                hostAggregatedStat.setAggIORead(hostAggregatedStat.getAggIORead() + vmDiskStat.getAggIORead());
                                hostAggregatedStat.setAggIOWrite(hostAggregatedStat.getAggIOWrite() + vmDiskStat.getAggIOWrite());
                                hostAggregatedStat.setAggBytesRead(hostAggregatedStat.getAggBytesRead() + vmDiskStat.getAggBytesRead());
                                hostAggregatedStat.setAggBytesWrite(hostAggregatedStat.getAggBytesWrite() + vmDiskStat.getAggBytesWrite());
                                aggregatedDiskStats.put(hostKey, hostAggregatedStat);
                            }
                        }
                    }
                    startIndex += 500;
                } while ((userStats != null) && !userStats.isEmpty());

                // loop over the user stats, create delta entries in the usage_disk helper table
                numAcctsProcessed = 0;
                usageVmDisks.clear();
                for (String key : aggregatedDiskStats.keySet()) {
                       UsageVmDiskVO currentVmDiskStats = null;
                    if (vmDiskStats != null) {
                        currentVmDiskStats = vmDiskUsages.get(key);
                    }

                    createVmDiskHelperEntry(aggregatedDiskStats.get(key), currentVmDiskStats, endDateMillis);
                    numAcctsProcessed++;
                }
                m_usageVmDiskDao.saveUsageVmDisks(usageVmDisks);

                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("created vm disk stats helper entries for " + numAcctsProcessed + " accts");
                }

                // commit the helper records, then start a new transaction
                usageTxn.commit();
                usageTxn.start();

                boolean parsed = false;
                numAcctsProcessed = 0;
                
                Date currentStartDate = startDate;
                Date currentEndDate = endDate;
                Date tempDate = endDate;
                
                Calendar aggregateCal = Calendar.getInstance(m_usageTimezone);
                
                while ((tempDate.after(startDate)) && ((tempDate.getTime() - startDate.getTime()) > 60000)){
                    currentEndDate = tempDate;
                    aggregateCal.setTime(tempDate);
                    aggregateCal.add(Calendar.MINUTE, -m_aggregationDuration);                                        
                    tempDate = aggregateCal.getTime();
                }
                
                while (!currentEndDate.after(endDate) || (currentEndDate.getTime() -endDate.getTime() < 60000)){
                    Long offset = Long.valueOf(0);
                    Long limit = Long.valueOf(500);

                    do {
                        Filter filter = new Filter(AccountVO.class, "id", true, offset, limit);
                        accounts = m_accountDao.listAll(filter);
                        if ((accounts != null) && !accounts.isEmpty()) {
                            for (AccountVO account : accounts) {
                                parsed = parseHelperTables(account, currentStartDate, currentEndDate);
                                numAcctsProcessed++;
                            }
                        }
                        offset = new Long(offset.longValue() + limit.longValue());
                    } while ((accounts != null) && !accounts.isEmpty());

                    if (s_logger.isDebugEnabled()) {
                        s_logger.debug("processed VM/Network Usage for " + numAcctsProcessed + " ACTIVE accts");
                    }
                    numAcctsProcessed = 0;

                    // reset offset
                    offset = Long.valueOf(0);

                    do {
                        Filter filter = new Filter(AccountVO.class, "id", true, offset, limit);

                        accounts = m_accountDao.findRecentlyDeletedAccounts(null, recentlyDeletedDate, filter);

                        if ((accounts != null) && !accounts.isEmpty()) {
                            for (AccountVO account : accounts) {
                                parsed = parseHelperTables(account, currentStartDate, currentEndDate);
                                List<Long> publicTemplates = m_usageDao.listPublicTemplatesByAccount(account.getId());
                                for(Long templateId : publicTemplates){
                                    //mark public templates owned by deleted accounts as deleted
                                    List<UsageStorageVO> storageVOs = m_usageStorageDao.listById(account.getId(), templateId, StorageTypes.TEMPLATE);
                                    if (storageVOs.size() > 1) {
                                        s_logger.warn("More that one usage entry for storage: " + templateId + " assigned to account: " + account.getId() + "; marking them all as deleted...");
                                    }
                                    for (UsageStorageVO storageVO : storageVOs) {
                                        if (s_logger.isDebugEnabled()) {
                                            s_logger.debug("deleting template: " + storageVO.getId() + " from account: " + storageVO.getAccountId());
                                        }
                                        storageVO.setDeleted(account.getRemoved()); 
                                        m_usageStorageDao.update(storageVO);
                                    }
                                }
                                numAcctsProcessed++;
                            }
                        }
                        offset = new Long(offset.longValue() + limit.longValue());
                    } while ((accounts != null) && !accounts.isEmpty());

                    currentStartDate = new Date(currentEndDate.getTime() + 1);
                    aggregateCal.setTime(currentEndDate);
                    aggregateCal.add(Calendar.MINUTE, m_aggregationDuration);                        
                    currentEndDate = aggregateCal.getTime();
                }
                
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("processed Usage for " + numAcctsProcessed + " RECENTLY DELETED accts");
                }

                // FIXME: we don't break the above loop if something fails to parse, so it gets reset every account,
                //        do we want to break out of processing accounts and rollback if there are errors?
                if (!parsed) {
                    usageTxn.rollback();
                } else {
                    success = true;
                }
            } catch (Exception ex) {
                s_logger.error("Exception in usage manager", ex);
                usageTxn.rollback();
            } finally {
                // everything seemed to work...set endDate as the last success date
                m_usageJobDao.updateJobSuccess(job.getId(), startDateMillis, endDateMillis, System.currentTimeMillis() - timeStart, success);

                // create a new job if this is a recurring job
                if (job.getJobType() == UsageJobVO.JOB_TYPE_RECURRING) {
                    m_usageJobDao.createNewJob(m_hostname, m_pid, UsageJobVO.JOB_TYPE_RECURRING);
                }
                usageTxn.commit();
                usageTxn.close();

                // switch back to CLOUD_DB
                TransactionLegacy swap = TransactionLegacy.open(TransactionLegacy.CLOUD_DB);
                if(!success){
                    _alertMgr.sendAlert(AlertManager.AlertType.ALERT_TYPE_USAGE_SERVER_RESULT, 0, new Long(0), "Usage job failed. Job id: "+job.getId(), "Usage job failed. Job id: "+job.getId());
                } else {
                    _alertMgr.clearAlert(AlertManager.AlertType.ALERT_TYPE_USAGE_SERVER_RESULT, 0, 0);
                }
                swap.close();
                
            }
        } catch (Exception e) {
            s_logger.error("Usage Manager error", e);
        }
    }
    
    private boolean parseHelperTables(AccountVO account, Date currentStartDate, Date currentEndDate){
        boolean parsed = false;

        parsed = VMInstanceUsageParser.parse(account, currentStartDate, currentEndDate);
        if (s_logger.isDebugEnabled()) {
            if (!parsed) {
                s_logger.debug("vm usage instances successfully parsed? " + parsed + " (for account: " + account.getAccountName() + ", id: " + account.getId() + ")");
            }
        }

        parsed = NetworkUsageParser.parse(account, currentStartDate, currentEndDate);
        if (s_logger.isDebugEnabled()) {
            if (!parsed) {
                s_logger.debug("network usage successfully parsed? " + parsed + " (for account: " + account.getAccountName() + ", id: " + account.getId() + ")");
            }
        }

        parsed = VmDiskUsageParser.parse(account, currentStartDate, currentEndDate);
        if (s_logger.isDebugEnabled()) {
            if (!parsed) {
                s_logger.debug("vm disk usage successfully parsed? " + parsed + " (for account: " + account.getAccountName() + ", id: " + account.getId() + ")");
            }
        }

        parsed = VolumeUsageParser.parse(account, currentStartDate, currentEndDate);
        if (s_logger.isDebugEnabled()) {
            if (!parsed) {
                s_logger.debug("volume usage successfully parsed? " + parsed + " (for account: " + account.getAccountName() + ", id: " + account.getId() + ")");
            }
        }

        parsed = StorageUsageParser.parse(account, currentStartDate, currentEndDate);
        if (s_logger.isDebugEnabled()) {
            if (!parsed) {
                s_logger.debug("storage usage successfully parsed? " + parsed + " (for account: " + account.getAccountName() + ", id: " + account.getId() + ")");
            }
        }

        parsed = SecurityGroupUsageParser.parse(account, currentStartDate, currentEndDate);
        if (s_logger.isDebugEnabled()) {
            if (!parsed) {
                s_logger.debug("Security Group usage successfully parsed? " + parsed + " (for account: " + account.getAccountName() + ", id: " + account.getId() + ")");
            }
        }
        
        parsed = LoadBalancerUsageParser.parse(account, currentStartDate, currentEndDate);
        if (s_logger.isDebugEnabled()) {
            if (!parsed) {
                s_logger.debug("load balancer usage successfully parsed? " + parsed + " (for account: " + account.getAccountName() + ", id: " + account.getId() + ")");
            }
        }
        
        parsed = PortForwardingUsageParser.parse(account, currentStartDate, currentEndDate);
        if (s_logger.isDebugEnabled()) {
            if (!parsed) {
                s_logger.debug("port forwarding usage successfully parsed? " + parsed + " (for account: " + account.getAccountName() + ", id: " + account.getId() + ")");
            }
        }
        
        parsed = NetworkOfferingUsageParser.parse(account, currentStartDate, currentEndDate);
        if (s_logger.isDebugEnabled()) {
            if (!parsed) {
                s_logger.debug("network offering usage successfully parsed? " + parsed + " (for account: " + account.getAccountName() + ", id: " + account.getId() + ")");
            }
        }
        
        parsed = IPAddressUsageParser.parse(account, currentStartDate, currentEndDate);
        if (s_logger.isDebugEnabled()) {
            if (!parsed) {
                s_logger.debug("IPAddress usage successfully parsed? " + parsed + " (for account: " + account.getAccountName() + ", id: " + account.getId() + ")");
            }
        }
        parsed = VPNUserUsageParser.parse(account, currentStartDate, currentEndDate);
        if (s_logger.isDebugEnabled()) {
            if (!parsed) {
                s_logger.debug("VPN user usage successfully parsed? " + parsed + " (for account: " + account.getAccountName() + ", id: " + account.getId() + ")");
            }
        }
        parsed = VMSnapshotUsageParser.parse(account, currentStartDate, currentEndDate);
        if (s_logger.isDebugEnabled()) {
            if (!parsed) {
                s_logger.debug("VM Snapshot usage successfully parsed? " + parsed + " (for account: " + account.getAccountName() + ", id: " + account.getId() + ")");
            }
        }
        return parsed;
    }

    private void createHelperRecord(UsageEventVO event) {
        String eventType = event.getType();
        if (isVMEvent(eventType)) {
            createVMHelperEvent(event);
        } else if (isIPEvent(eventType)) {
            createIPHelperEvent(event);
        } else if (isVolumeEvent(eventType)) {
            createVolumeHelperEvent(event);
        } else if (isTemplateEvent(eventType)) {
            createTemplateHelperEvent(event);
        } else if (isISOEvent(eventType)) {
            createISOHelperEvent(event);
        } else if (isSnapshotEvent(eventType)) {
            createSnapshotHelperEvent(event);
        } else if (isLoadBalancerEvent(eventType)) {
            createLoadBalancerHelperEvent(event);
        } else if (isPortForwardingEvent(eventType)) {
            createPortForwardingHelperEvent(event);
        } else if (isNetworkOfferingEvent(eventType)) {
            createNetworkOfferingEvent(event);
        } else if (isVPNUserEvent(eventType)) {
            createVPNUserEvent(event);
        } else if (isSecurityGroupEvent(eventType)) {
            createSecurityGroupEvent(event);
        } else if (isVmSnapshotEvent(eventType)){
            createVMSnapshotEvent(event);
        }
    }

    private boolean isVMEvent(String eventType) {
        if (eventType == null) return false;
        return eventType.startsWith("VM.");
    }

    private boolean isIPEvent(String eventType) {
        if (eventType == null) return false;
        return eventType.startsWith("NET.IP");
    }
    
    private boolean isVolumeEvent(String eventType) {
        if (eventType == null) return false;
        return (eventType.equals(EventTypes.EVENT_VOLUME_CREATE) ||
                eventType.equals(EventTypes.EVENT_VOLUME_DELETE));
    }

    private boolean isTemplateEvent(String eventType) {
        if (eventType == null) return false;
        return (eventType.equals(EventTypes.EVENT_TEMPLATE_CREATE) ||
                eventType.equals(EventTypes.EVENT_TEMPLATE_COPY) ||
                eventType.equals(EventTypes.EVENT_TEMPLATE_DELETE));
    }
    
    private boolean isISOEvent(String eventType) {
        if (eventType == null) return false;
        return (eventType.equals(EventTypes.EVENT_ISO_CREATE) ||
                eventType.equals(EventTypes.EVENT_ISO_COPY) ||
                eventType.equals(EventTypes.EVENT_ISO_DELETE));
    }
    
    private boolean isSnapshotEvent(String eventType) {
        if (eventType == null) return false;
        return (eventType.equals(EventTypes.EVENT_SNAPSHOT_CREATE) ||
                eventType.equals(EventTypes.EVENT_SNAPSHOT_DELETE));
    }
    
    private boolean isLoadBalancerEvent(String eventType) {
        if (eventType == null) return false;
        return eventType.startsWith("LB.");
    }
    
    private boolean isPortForwardingEvent(String eventType) {
        if (eventType == null) return false;
        return eventType.startsWith("NET.RULE");
    }
    
    private boolean isNetworkOfferingEvent(String eventType) {
        if (eventType == null) return false;
        return (eventType.equals(EventTypes.EVENT_NETWORK_OFFERING_CREATE) ||
                eventType.equals(EventTypes.EVENT_NETWORK_OFFERING_DELETE) ||
                eventType.equals(EventTypes.EVENT_NETWORK_OFFERING_ASSIGN) ||
                eventType.equals(EventTypes.EVENT_NETWORK_OFFERING_REMOVE));
    }
    
    private boolean isVPNUserEvent(String eventType) {
        if (eventType == null) return false;
        return eventType.startsWith("VPN.USER");
    }

    private boolean isSecurityGroupEvent(String eventType) {
        if (eventType == null) return false;
        return (eventType.equals(EventTypes.EVENT_SECURITY_GROUP_ASSIGN) ||
                eventType.equals(EventTypes.EVENT_SECURITY_GROUP_REMOVE));
    }

    private boolean isVmSnapshotEvent(String eventType){
        if (eventType == null) return false;
        return (eventType.equals(EventTypes.EVENT_VM_SNAPSHOT_CREATE) ||
                eventType.equals(EventTypes.EVENT_VM_SNAPSHOT_DELETE));
    }
    private void createVMHelperEvent(UsageEventVO event) {

        // One record for handling VM.START and VM.STOP
        // One record for handling VM.CREATE and VM.DESTROY
        // VM events have the parameter "id=<virtualMachineId>"
        long vmId = event.getResourceId();
        Long soId = event.getOfferingId();; // service offering id
        long zoneId = event.getZoneId();
        String vmName = event.getResourceName();

        if (EventTypes.EVENT_VM_START.equals(event.getType())) {
            // create a new usage_vm_instance row for this VM
            try {
                
                SearchCriteria<UsageVMInstanceVO> sc = m_usageInstanceDao.createSearchCriteria();
                sc.addAnd("vmInstanceId", SearchCriteria.Op.EQ, Long.valueOf(vmId));
                sc.addAnd("endDate", SearchCriteria.Op.NULL);
                sc.addAnd("usageType", SearchCriteria.Op.EQ, UsageTypes.RUNNING_VM);
                List<UsageVMInstanceVO> usageInstances = m_usageInstanceDao.search(sc, null);
                if (usageInstances != null) {
                    if (usageInstances.size() > 0) {
                        s_logger.error("found entries for a vm running with id: " + vmId + ", which are not stopped. Ending them all...");
                        for (UsageVMInstanceVO usageInstance : usageInstances) {
                            usageInstance.setEndDate(event.getCreateDate());
                            m_usageInstanceDao.update(usageInstance);
                        }
                    }
                }
                
                sc = m_usageInstanceDao.createSearchCriteria();
                sc.addAnd("vmInstanceId", SearchCriteria.Op.EQ, Long.valueOf(vmId));
                sc.addAnd("endDate", SearchCriteria.Op.NULL);
                sc.addAnd("usageType", SearchCriteria.Op.EQ, UsageTypes.ALLOCATED_VM);
                usageInstances = m_usageInstanceDao.search(sc, null);
                if (usageInstances == null || (usageInstances.size() == 0)) {
                    s_logger.error("Cannot find allocated vm entry for a vm running with id: " + vmId);
                } else if (usageInstances.size() == 1) {
                    UsageVMInstanceVO usageInstance = usageInstances.get(0);
                    if(usageInstance.getSerivceOfferingId() != soId){
                        //Service Offering changed after Vm creation
                        //End current Allocated usage and create new Allocated Vm entry with new soId
                        usageInstance.setEndDate(event.getCreateDate());
                        m_usageInstanceDao.update(usageInstance);
                        usageInstance.setServiceOfferingId(soId);
                        usageInstance.setStartDate(event.getCreateDate());
                        usageInstance.setEndDate(null);
                        populateDynamicComputeOfferingDetailsAndPersist(usageInstance, event.getId());
                    }
                }


                Long templateId = event.getTemplateId();
                String hypervisorType = event.getResourceType();

                // add this VM to the usage helper table
                UsageVMInstanceVO usageInstanceNew = new UsageVMInstanceVO(UsageTypes.RUNNING_VM, zoneId, event.getAccountId(), vmId, vmName,
                        soId, templateId, hypervisorType, event.getCreateDate(), null);
                populateDynamicComputeOfferingDetailsAndPersist(usageInstanceNew, event.getId());
            } catch (Exception ex) {
                s_logger.error("Error saving usage instance for vm: " + vmId, ex);
            }
        } else if (EventTypes.EVENT_VM_STOP.equals(event.getType())) {
            // find the latest usage_vm_instance row, update the stop date (should be null) to the event date
            // FIXME: search criteria needs to have some kind of type information so we distinguish between START/STOP and CREATE/DESTROY
            SearchCriteria<UsageVMInstanceVO> sc = m_usageInstanceDao.createSearchCriteria();
            sc.addAnd("vmInstanceId", SearchCriteria.Op.EQ, Long.valueOf(vmId));
            sc.addAnd("endDate", SearchCriteria.Op.NULL);
            sc.addAnd("usageType", SearchCriteria.Op.EQ, UsageTypes.RUNNING_VM);
            List<UsageVMInstanceVO> usageInstances = m_usageInstanceDao.search(sc, null);
            if (usageInstances != null) {
                if (usageInstances.size() > 1) {
                    s_logger.warn("found multiple entries for a vm running with id: " + vmId + ", ending them all...");
                }
                for (UsageVMInstanceVO usageInstance : usageInstances) {
                    usageInstance.setEndDate(event.getCreateDate());
                    // TODO: UsageVMInstanceVO should have an ID field and we should do updates through that field since we are really
                    //       updating one row at a time here
                    m_usageInstanceDao.update(usageInstance);
                }
            }
        } else if (EventTypes.EVENT_VM_CREATE.equals(event.getType())) {
            try {
                Long templateId = event.getTemplateId();
                String hypervisorType = event.getResourceType();

                // add this VM to the usage helper table
                UsageVMInstanceVO usageInstanceNew = new UsageVMInstanceVO(UsageTypes.ALLOCATED_VM, zoneId, event.getAccountId(), vmId, vmName,
                        soId, templateId, hypervisorType, event.getCreateDate(), null);
                populateDynamicComputeOfferingDetailsAndPersist(usageInstanceNew, event.getId());
            } catch (Exception ex) {
                s_logger.error("Error saving usage instance for vm: " + vmId, ex);
            }
        } else if (EventTypes.EVENT_VM_DESTROY.equals(event.getType())) {
            SearchCriteria<UsageVMInstanceVO> sc = m_usageInstanceDao.createSearchCriteria();
            sc.addAnd("vmInstanceId", SearchCriteria.Op.EQ, Long.valueOf(vmId));
            sc.addAnd("endDate", SearchCriteria.Op.NULL);
            sc.addAnd("usageType", SearchCriteria.Op.EQ, UsageTypes.ALLOCATED_VM);
            List<UsageVMInstanceVO> usageInstances = m_usageInstanceDao.search(sc, null);
            if (usageInstances != null) {
                if (usageInstances.size() > 1) {
                    s_logger.warn("found multiple entries for a vm allocated with id: " + vmId + ", detroying them all...");
                }
                for (UsageVMInstanceVO usageInstance : usageInstances) {
                    usageInstance.setEndDate(event.getCreateDate());
                    m_usageInstanceDao.update(usageInstance);
                }
            }
        } else if (EventTypes.EVENT_VM_UPGRADE.equals(event.getType())) {
            SearchCriteria<UsageVMInstanceVO> sc = m_usageInstanceDao.createSearchCriteria();
            sc.addAnd("vmInstanceId", SearchCriteria.Op.EQ, Long.valueOf(vmId));
            sc.addAnd("endDate", SearchCriteria.Op.NULL);
            sc.addAnd("usageType", SearchCriteria.Op.EQ, UsageTypes.ALLOCATED_VM);
            List<UsageVMInstanceVO> usageInstances = m_usageInstanceDao.search(sc, null);
            if (usageInstances != null) {
                if (usageInstances.size() > 1) {
                    s_logger.warn("found multiple entries for a vm allocated with id: " + vmId + ", updating end_date for all of them...");
                }
                for (UsageVMInstanceVO usageInstance : usageInstances) {
                    usageInstance.setEndDate(event.getCreateDate());
                    m_usageInstanceDao.update(usageInstance);
                }
            }

            Long templateId = event.getTemplateId();
            String hypervisorType = event.getResourceType();
            // add this VM to the usage helper table
            UsageVMInstanceVO usageInstanceNew = new UsageVMInstanceVO(UsageTypes.ALLOCATED_VM, zoneId, event.getAccountId(), vmId, vmName,
                    soId, templateId, hypervisorType, event.getCreateDate(), null);
            populateDynamicComputeOfferingDetailsAndPersist(usageInstanceNew, event.getId());
        } else if (EventTypes.EVENT_VM_DYNAMIC_SCALE.equals(event.getType())) {
            // Ending the running vm event
            SearchCriteria<UsageVMInstanceVO> sc = m_usageInstanceDao.createSearchCriteria();
            sc.addAnd("vmInstanceId", SearchCriteria.Op.EQ, Long.valueOf(vmId));
            sc.addAnd("endDate", SearchCriteria.Op.NULL);
            sc.addAnd("usageType", SearchCriteria.Op.EQ, UsageTypes.RUNNING_VM);
            List<UsageVMInstanceVO> usageInstances = m_usageInstanceDao.search(sc, null);
            if (usageInstances != null) {
                if (usageInstances.size() > 1) {
                    s_logger.warn("found multiple entries for a vm running with id: " + vmId + ", ending them all...");
                }
                for (UsageVMInstanceVO usageInstance : usageInstances) {
                    usageInstance.setEndDate(event.getCreateDate());
                    m_usageInstanceDao.update(usageInstance);
                }
            }

            sc = m_usageInstanceDao.createSearchCriteria();
            sc.addAnd("vmInstanceId", SearchCriteria.Op.EQ, Long.valueOf(vmId));
            sc.addAnd("endDate", SearchCriteria.Op.NULL);
            sc.addAnd("usageType", SearchCriteria.Op.EQ, UsageTypes.ALLOCATED_VM);
            usageInstances = m_usageInstanceDao.search(sc, null);
            if (usageInstances == null || (usageInstances.size() == 0)) {
                s_logger.error("Cannot find allocated vm entry for a vm running with id: " + vmId);
            } else if (usageInstances.size() == 1) {
                UsageVMInstanceVO usageInstance = usageInstances.get(0);
                if(usageInstance.getSerivceOfferingId() != soId){
                    //Service Offering changed after Vm creation
                    //End current Allocated usage and create new Allocated Vm entry with new soId
                    usageInstance.setEndDate(event.getCreateDate());
                    m_usageInstanceDao.update(usageInstance);
                    usageInstance.setServiceOfferingId(soId);
                    usageInstance.setStartDate(event.getCreateDate());
                    usageInstance.setEndDate(null);
                    populateDynamicComputeOfferingDetailsAndPersist(usageInstance, event.getId());
                }
            }

            Long templateId = event.getTemplateId();
            String hypervisorType = event.getResourceType();

            // add this VM to the usage helper table with new service offering Id
            UsageVMInstanceVO usageInstanceNew = new UsageVMInstanceVO(UsageTypes.RUNNING_VM, zoneId, event.getAccountId(), vmId, vmName,
                    soId, templateId, hypervisorType, event.getCreateDate(), null);
            populateDynamicComputeOfferingDetailsAndPersist(usageInstanceNew, event.getId());
        }
    }

    private void populateDynamicComputeOfferingDetailsAndPersist(UsageVMInstanceVO usageInstance, Long eventId) {

        //populate the cpu, memory and cpuSpeed of the vm when created from a dynamic offering.
        UsageEventDetailsVO cpuNumber = _usageEventDetailsDao.findDetail(eventId, UsageEventVO.DynamicParameters.cpuNumber.name());
        if (cpuNumber != null) {
            usageInstance.setCpuCores(Long.parseLong(cpuNumber.getValue()));
        } else {
            usageInstance.setCpuCores(null);
        }

        UsageEventDetailsVO cpuSpeed = _usageEventDetailsDao.findDetail(eventId, UsageEventVO.DynamicParameters.cpuSpeed.name());
        if (cpuSpeed != null) {
            usageInstance.setCpuSpeed(Long.parseLong(cpuSpeed.getValue()));
        } else {
            usageInstance.setCpuSpeed(null);
        }

        UsageEventDetailsVO memory = _usageEventDetailsDao.findDetail(eventId, UsageEventVO.DynamicParameters.memory.name());
        if (memory != null) {
            usageInstance.setMemory(Long.parseLong(memory.getValue()));
        } else {
            usageInstance.setMemory(null);
        }
        m_usageInstanceDao.persist(usageInstance);
    }

    private void createNetworkHelperEntry(UserStatisticsVO userStat, UsageNetworkVO usageNetworkStats, long timestamp) {
        long currentAccountedBytesSent = 0L;
        long currentAccountedBytesReceived = 0L;
        if (usageNetworkStats != null) {
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("getting current accounted bytes for... accountId: " + usageNetworkStats.getAccountId() + " in zone: " + userStat.getDataCenterId() + "; abr: " + usageNetworkStats.getAggBytesReceived() +
                        "; abs: " + usageNetworkStats.getAggBytesSent());
            }
            currentAccountedBytesSent = usageNetworkStats.getAggBytesSent();
            currentAccountedBytesReceived = usageNetworkStats.getAggBytesReceived();
        }
        long bytesSent = userStat.getAggBytesSent()  - currentAccountedBytesSent;
        long bytesReceived = userStat.getAggBytesReceived() - currentAccountedBytesReceived;

        if (bytesSent < 0) {
            s_logger.warn("Calculated negative value for bytes sent: " + bytesSent + ", user stats say: " + userStat.getAggBytesSent() + ", previous network usage was: " + currentAccountedBytesSent);
            bytesSent = 0;
        }
        if (bytesReceived < 0) {
            s_logger.warn("Calculated negative value for bytes received: " + bytesReceived + ", user stats say: " + userStat.getAggBytesReceived() + ", previous network usage was: " + currentAccountedBytesReceived);
            bytesReceived = 0;
        }

        long hostId = 0;
        
        if(userStat.getDeviceId() != null){
            hostId = userStat.getDeviceId(); 
        }
        
        UsageNetworkVO usageNetworkVO = new UsageNetworkVO(userStat.getAccountId(), userStat.getDataCenterId(), hostId, userStat.getDeviceType(), userStat.getNetworkId(), bytesSent, bytesReceived,
                userStat.getAggBytesReceived(), userStat.getAggBytesSent(), timestamp);
        if (s_logger.isDebugEnabled()) {
            s_logger.debug("creating networkHelperEntry... accountId: " + userStat.getAccountId() + " in zone: " + userStat.getDataCenterId() + "; abr: " + userStat.getAggBytesReceived() + "; abs: " + userStat.getAggBytesSent() +
                    "; curABS: " + currentAccountedBytesSent + "; curABR: " + currentAccountedBytesReceived + "; ubs: " + bytesSent + "; ubr: " + bytesReceived);
        }
        usageNetworks.add(usageNetworkVO);
    }

    private void createVmDiskHelperEntry(VmDiskStatisticsVO vmDiskStat, UsageVmDiskVO usageVmDiskStat, long timestamp) {
        long currentAccountedIORead = 0L;
        long currentAccountedIOWrite = 0L;
        long currentAccountedBytesRead = 0L;
        long currentAccountedBytesWrite = 0L;
        if (usageVmDiskStat != null) {
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("getting current accounted bytes for... accountId: " + usageVmDiskStat.getAccountId() + " in zone: " + vmDiskStat.getDataCenterId() + "; aiw: " + vmDiskStat.getAggIOWrite() +
                        "; air: " + usageVmDiskStat.getAggIORead() + "; abw: " + vmDiskStat.getAggBytesWrite() + "; abr: " + usageVmDiskStat.getAggBytesRead());
            }
            currentAccountedIORead = usageVmDiskStat.getAggIORead();
            currentAccountedIOWrite = usageVmDiskStat.getAggIOWrite();
            currentAccountedBytesRead = usageVmDiskStat.getAggBytesRead();
            currentAccountedBytesWrite = usageVmDiskStat.getAggBytesWrite();
        }
        long ioRead = vmDiskStat.getAggIORead()  - currentAccountedIORead;
        long ioWrite = vmDiskStat.getAggIOWrite() - currentAccountedIOWrite;
        long bytesRead = vmDiskStat.getAggBytesRead()  - currentAccountedBytesRead;
        long bytesWrite = vmDiskStat.getAggBytesWrite() - currentAccountedBytesWrite;

        if (ioRead < 0) {
            s_logger.warn("Calculated negative value for io read: " + ioRead + ", vm disk stats say: " + vmDiskStat.getAggIORead() + ", previous vm disk usage was: " + currentAccountedIORead);
            ioRead = 0;
        }
        if (ioWrite < 0) {
            s_logger.warn("Calculated negative value for io write: " + ioWrite + ", vm disk stats say: " + vmDiskStat.getAggIOWrite() + ", previous vm disk usage was: " + currentAccountedIOWrite);
            ioWrite = 0;
        }
        if (bytesRead < 0) {
            s_logger.warn("Calculated negative value for bytes read: " + bytesRead + ", vm disk stats say: " + vmDiskStat.getAggBytesRead() + ", previous vm disk usage was: " + currentAccountedBytesRead);
            bytesRead = 0;
        }
        if (bytesWrite < 0) {
            s_logger.warn("Calculated negative value for bytes write: " + bytesWrite + ", vm disk stats say: " + vmDiskStat.getAggBytesWrite() + ", previous vm disk usage was: " + currentAccountedBytesWrite);
            bytesWrite = 0;
        }

        long vmId = 0;

        if(vmDiskStat.getVmId() != null){
               vmId = vmDiskStat.getVmId();
        }

        UsageVmDiskVO usageVmDiskVO = new UsageVmDiskVO(vmDiskStat.getAccountId(), vmDiskStat.getDataCenterId(), vmId, vmDiskStat.getVolumeId(), ioRead, ioWrite,
                       vmDiskStat.getAggIORead(), vmDiskStat.getAggIOWrite(), bytesRead, bytesWrite, vmDiskStat.getAggBytesRead(), vmDiskStat.getAggBytesWrite(), timestamp);
        if (s_logger.isDebugEnabled()) {
            s_logger.debug("creating vmDiskHelperEntry... accountId: " + vmDiskStat.getAccountId() + " in zone: " + vmDiskStat.getDataCenterId() + "; aiw: " + vmDiskStat.getAggIOWrite() + "; air: " + vmDiskStat.getAggIORead() +
                    "; curAIR: " + currentAccountedIORead + "; curAIW: " + currentAccountedIOWrite + "; uir: " + ioRead + "; uiw: " + ioWrite + "; abw: " + vmDiskStat.getAggBytesWrite() + "; abr: " + vmDiskStat.getAggBytesRead() +
                    "; curABR: " + currentAccountedBytesRead + "; curABW: " + currentAccountedBytesWrite + "; ubr: " + bytesRead + "; ubw: " + bytesWrite);
        }
        usageVmDisks.add(usageVmDiskVO);
    }

    private void createIPHelperEvent(UsageEventVO event) {

        String ipAddress = event.getResourceName();

        if (EventTypes.EVENT_NET_IP_ASSIGN.equals(event.getType())) {
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("assigning ip address: " + ipAddress + " to account: " + event.getAccountId());
            }
            Account acct = m_accountDao.findByIdIncludingRemoved(event.getAccountId());
            long zoneId = event.getZoneId();
            long id = event.getResourceId();
            long sourceNat = event.getSize();
            boolean isSourceNat = (sourceNat == 1) ? true : false ;
            boolean isSystem = (event.getTemplateId() == null || event.getTemplateId() == 0) ? false : true ;
            UsageIPAddressVO ipAddressVO = new UsageIPAddressVO(id, event.getAccountId(), acct.getDomainId(), zoneId, ipAddress, isSourceNat, isSystem, event.getCreateDate(), null);
            m_usageIPAddressDao.persist(ipAddressVO);
        } else if (EventTypes.EVENT_NET_IP_RELEASE.equals(event.getType())) {
            SearchCriteria<UsageIPAddressVO> sc = m_usageIPAddressDao.createSearchCriteria();
            sc.addAnd("accountId", SearchCriteria.Op.EQ, event.getAccountId());
            sc.addAnd("address", SearchCriteria.Op.EQ, ipAddress);
            sc.addAnd("released", SearchCriteria.Op.NULL);
            List<UsageIPAddressVO> ipAddressVOs = m_usageIPAddressDao.search(sc, null);
            if (ipAddressVOs.size() > 1) {
                s_logger.warn("More that one usage entry for ip address: " + ipAddress + " assigned to account: " + event.getAccountId() + "; marking them all as released...");
            }
            for (UsageIPAddressVO ipAddressVO : ipAddressVOs) {
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("releasing ip address: " + ipAddressVO.getAddress() + " from account: " + ipAddressVO.getAccountId());
                }
                ipAddressVO.setReleased(event.getCreateDate()); // there really shouldn't be more than one
                m_usageIPAddressDao.update(ipAddressVO);
            }
        }
    }

    private void createVolumeHelperEvent(UsageEventVO event) {
        
        Long doId = -1L;
        long zoneId = -1L;
        Long templateId = -1L;
        long size = -1L;
        
        long volId = event.getResourceId();
        if (EventTypes.EVENT_VOLUME_CREATE.equals(event.getType())) {
            doId = event.getOfferingId();
            zoneId = event.getZoneId();
            templateId = event.getTemplateId();
            size = event.getSize();
        }

        if (EventTypes.EVENT_VOLUME_CREATE.equals(event.getType())) {
            SearchCriteria<UsageVolumeVO> sc = m_usageVolumeDao.createSearchCriteria();
            sc.addAnd("accountId", SearchCriteria.Op.EQ, event.getAccountId());
            sc.addAnd("id", SearchCriteria.Op.EQ, volId);
            sc.addAnd("deleted", SearchCriteria.Op.NULL);
            List<UsageVolumeVO> volumesVOs = m_usageVolumeDao.search(sc, null);
            if (volumesVOs.size() > 0) {
                //This is a safeguard to avoid double counting of volumes.
                s_logger.error("Found duplicate usage entry for volume: " + volId + " assigned to account: " + event.getAccountId() + "; marking as deleted...");
            }
            for (UsageVolumeVO volumesVO : volumesVOs) {
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("deleting volume: " + volumesVO.getId() + " from account: " + volumesVO.getAccountId());
                }
                volumesVO.setDeleted(event.getCreateDate());
                m_usageVolumeDao.update(volumesVO);
            }
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("create volume with id : " + volId + " for account: " + event.getAccountId());
            }
            Account acct = m_accountDao.findByIdIncludingRemoved(event.getAccountId());
            UsageVolumeVO volumeVO = new UsageVolumeVO(volId, zoneId, event.getAccountId(), acct.getDomainId(), doId, templateId, size, event.getCreateDate(), null);
            m_usageVolumeDao.persist(volumeVO);
        } else if (EventTypes.EVENT_VOLUME_DELETE.equals(event.getType())) {
            SearchCriteria<UsageVolumeVO> sc = m_usageVolumeDao.createSearchCriteria();
            sc.addAnd("accountId", SearchCriteria.Op.EQ, event.getAccountId());
            sc.addAnd("id", SearchCriteria.Op.EQ, volId);
            sc.addAnd("deleted", SearchCriteria.Op.NULL);
            List<UsageVolumeVO> volumesVOs = m_usageVolumeDao.search(sc, null);
            if (volumesVOs.size() > 1) {
                s_logger.warn("More that one usage entry for volume: " + volId + " assigned to account: " + event.getAccountId() + "; marking them all as deleted...");
            }
            for (UsageVolumeVO volumesVO : volumesVOs) {
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("deleting volume: " + volumesVO.getId() + " from account: " + volumesVO.getAccountId());
                }
                volumesVO.setDeleted(event.getCreateDate()); // there really shouldn't be more than one
                m_usageVolumeDao.update(volumesVO);
            }
        }
    }

    private void createTemplateHelperEvent(UsageEventVO event) {
        
        long templateId = -1L;
        long zoneId = -1L;
        long templateSize = -1L;

        templateId = event.getResourceId();
        zoneId = event.getZoneId();
        if (EventTypes.EVENT_TEMPLATE_CREATE.equals(event.getType()) || EventTypes.EVENT_TEMPLATE_COPY.equals(event.getType())) {
            templateSize = event.getSize();
            if(templateSize < 1){
                s_logger.error("Incorrect size for template with Id "+templateId);
                return;
            }
            if(zoneId == -1L){
                s_logger.error("Incorrect zoneId for template with Id "+templateId);
                return;
            }
        }

        if (EventTypes.EVENT_TEMPLATE_CREATE.equals(event.getType()) || EventTypes.EVENT_TEMPLATE_COPY.equals(event.getType())) {
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("create template with id : " + templateId + " for account: " + event.getAccountId());
            }
            List<UsageStorageVO> storageVOs = m_usageStorageDao.listByIdAndZone(event.getAccountId(), templateId, StorageTypes.TEMPLATE, zoneId);
            if (storageVOs.size() > 0) {
                s_logger.warn("Usage entry for Template: " + templateId + " assigned to account: " + event.getAccountId() + "already exists in zone "+zoneId);
                return;
            }
            Account acct = m_accountDao.findByIdIncludingRemoved(event.getAccountId());
            UsageStorageVO storageVO = new UsageStorageVO(templateId, zoneId, event.getAccountId(), acct.getDomainId(), StorageTypes.TEMPLATE, event.getTemplateId(),
                                        templateSize, event.getVirtualSize(), event.getCreateDate(), null);
            m_usageStorageDao.persist(storageVO);
        } else if (EventTypes.EVENT_TEMPLATE_DELETE.equals(event.getType())) {
            List<UsageStorageVO> storageVOs;
            if(zoneId != -1L){
                storageVOs = m_usageStorageDao.listByIdAndZone(event.getAccountId(), templateId, StorageTypes.TEMPLATE, zoneId);
            } else {
                storageVOs = m_usageStorageDao.listById(event.getAccountId(), templateId, StorageTypes.TEMPLATE);
            }
            if (storageVOs.size() > 1) {
                s_logger.warn("More that one usage entry for storage: " + templateId + " assigned to account: " + event.getAccountId() + "; marking them all as deleted...");
            }
            for (UsageStorageVO storageVO : storageVOs) {
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("deleting template: " + storageVO.getId() + " from account: " + storageVO.getAccountId());
                }
                storageVO.setDeleted(event.getCreateDate()); // there really shouldn't be more than one
                m_usageStorageDao.update(storageVO);
            }
        }
    }
    
    private void createISOHelperEvent(UsageEventVO event) {
        long isoSize = -1L;

        long isoId = event.getResourceId();
        long zoneId = event.getZoneId();
        if (EventTypes.EVENT_ISO_CREATE.equals(event.getType()) || EventTypes.EVENT_ISO_COPY.equals(event.getType())) {
            isoSize = event.getSize();
        }

        if (EventTypes.EVENT_ISO_CREATE.equals(event.getType()) || EventTypes.EVENT_ISO_COPY.equals(event.getType())) {
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("create iso with id : " + isoId + " for account: " + event.getAccountId());
            }
             List<UsageStorageVO> storageVOs = m_usageStorageDao.listByIdAndZone(event.getAccountId(), isoId, StorageTypes.ISO, zoneId);
             if (storageVOs.size() > 0) {
                 s_logger.warn("Usage entry for ISO: " + isoId + " assigned to account: " + event.getAccountId() + "already exists in zone "+zoneId);
                 return;
             }
            Account acct = m_accountDao.findByIdIncludingRemoved(event.getAccountId());
            UsageStorageVO storageVO = new UsageStorageVO( isoId, zoneId, event.getAccountId(), acct.getDomainId(), StorageTypes.ISO, null,
                    isoSize, isoSize, event.getCreateDate(), null);
            m_usageStorageDao.persist(storageVO);
        } else if (EventTypes.EVENT_ISO_DELETE.equals(event.getType())) {
            List<UsageStorageVO> storageVOs;
            if(zoneId != -1L){
                storageVOs = m_usageStorageDao.listByIdAndZone(event.getAccountId(), isoId, StorageTypes.ISO, zoneId);
            } else {
                storageVOs = m_usageStorageDao.listById(event.getAccountId(), isoId, StorageTypes.ISO);
            }
            
            if (storageVOs.size() > 1) {
                s_logger.warn("More that one usage entry for storage: " + isoId + " assigned to account: " + event.getAccountId() + "; marking them all as deleted...");
            }
            for (UsageStorageVO storageVO : storageVOs) {
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("deleting iso: " + storageVO.getId() + " from account: " + storageVO.getAccountId());
                }
                storageVO.setDeleted(event.getCreateDate()); // there really shouldn't be more than one
                m_usageStorageDao.update(storageVO);
            }
        }
    }
    
    private void createSnapshotHelperEvent(UsageEventVO event) {
        long snapSize = -1L;
        long zoneId = -1L;

        long snapId = event.getResourceId();
        if (EventTypes.EVENT_SNAPSHOT_CREATE.equals(event.getType())) {
            snapSize = event.getSize();
            zoneId = event.getZoneId();
        }

        if (EventTypes.EVENT_SNAPSHOT_CREATE.equals(event.getType())) {
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("create snapshot with id : " + snapId + " for account: " + event.getAccountId());
            }
            Account acct = m_accountDao.findByIdIncludingRemoved(event.getAccountId());
            UsageStorageVO storageVO = new UsageStorageVO( snapId, zoneId, event.getAccountId(), acct.getDomainId(), StorageTypes.SNAPSHOT, null,
                    snapSize, event.getCreateDate(), null);
            m_usageStorageDao.persist(storageVO);
        } else if (EventTypes.EVENT_SNAPSHOT_DELETE.equals(event.getType())) {
            List<UsageStorageVO> storageVOs = m_usageStorageDao.listById(event.getAccountId(), snapId, StorageTypes.SNAPSHOT);
            if (storageVOs.size() > 1) {
                s_logger.warn("More that one usage entry for storage: " + snapId + " assigned to account: " + event.getAccountId() + "; marking them all as deleted...");
            }
            for (UsageStorageVO storageVO : storageVOs) {
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("deleting snapshot: " + storageVO.getId() + " from account: " + storageVO.getAccountId());
                }
                storageVO.setDeleted(event.getCreateDate()); // there really shouldn't be more than one
                m_usageStorageDao.update(storageVO);
            }
        }
    }
    
    private void createLoadBalancerHelperEvent(UsageEventVO event) {

        long zoneId = -1L;

        long id = event.getResourceId();

        if (EventTypes.EVENT_LOAD_BALANCER_CREATE.equals(event.getType())) {
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("Creating load balancer : " + id + " for account: " + event.getAccountId());
            }
            zoneId = event.getZoneId();
            Account acct = m_accountDao.findByIdIncludingRemoved(event.getAccountId());
            UsageLoadBalancerPolicyVO lbVO = new UsageLoadBalancerPolicyVO(id, zoneId, event.getAccountId(), acct.getDomainId(),
                                                event.getCreateDate(), null);
            m_usageLoadBalancerPolicyDao.persist(lbVO);
        } else if (EventTypes.EVENT_LOAD_BALANCER_DELETE.equals(event.getType())) {
            SearchCriteria<UsageLoadBalancerPolicyVO> sc = m_usageLoadBalancerPolicyDao.createSearchCriteria();
            sc.addAnd("accountId", SearchCriteria.Op.EQ, event.getAccountId());
            sc.addAnd("id", SearchCriteria.Op.EQ, id);
            sc.addAnd("deleted", SearchCriteria.Op.NULL);
            List<UsageLoadBalancerPolicyVO> lbVOs = m_usageLoadBalancerPolicyDao.search(sc, null);
            if (lbVOs.size() > 1) {
                s_logger.warn("More that one usage entry for load balancer policy: " + id + " assigned to account: " + event.getAccountId() + "; marking them all as deleted...");
            }
            for (UsageLoadBalancerPolicyVO lbVO : lbVOs) {
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("deleting load balancer policy: " + lbVO.getId() + " from account: " + lbVO.getAccountId());
                }
                lbVO.setDeleted(event.getCreateDate()); // there really shouldn't be more than one
                m_usageLoadBalancerPolicyDao.update(lbVO);
            }
        }
    }
    
    private void createPortForwardingHelperEvent(UsageEventVO event) {

        long zoneId = -1L;

        long id = event.getResourceId();

        if (EventTypes.EVENT_NET_RULE_ADD.equals(event.getType())) {
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("Creating port forwarding rule : " + id + " for account: " + event.getAccountId());
            }
            zoneId = event.getZoneId();
            Account acct = m_accountDao.findByIdIncludingRemoved(event.getAccountId());
            UsagePortForwardingRuleVO pfVO = new UsagePortForwardingRuleVO(id, zoneId, event.getAccountId(), acct.getDomainId(),
                                                event.getCreateDate(), null);
            m_usagePortForwardingRuleDao.persist(pfVO);
        } else if (EventTypes.EVENT_NET_RULE_DELETE.equals(event.getType())) {
            SearchCriteria<UsagePortForwardingRuleVO> sc = m_usagePortForwardingRuleDao.createSearchCriteria();
            sc.addAnd("accountId", SearchCriteria.Op.EQ, event.getAccountId());
            sc.addAnd("id", SearchCriteria.Op.EQ, id);
            sc.addAnd("deleted", SearchCriteria.Op.NULL);
            List<UsagePortForwardingRuleVO> pfVOs = m_usagePortForwardingRuleDao.search(sc, null);
            if (pfVOs.size() > 1) {
                s_logger.warn("More that one usage entry for port forwarding rule: " + id + " assigned to account: " + event.getAccountId() + "; marking them all as deleted...");
            }
            for (UsagePortForwardingRuleVO pfVO : pfVOs) {
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("deleting port forwarding rule: " + pfVO.getId() + " from account: " + pfVO.getAccountId());
                }
                pfVO.setDeleted(event.getCreateDate()); // there really shouldn't be more than one
                m_usagePortForwardingRuleDao.update(pfVO);
            }
        }
    }

    private void createNetworkOfferingEvent(UsageEventVO event) {

        long zoneId = -1L;

        long vmId = event.getResourceId();
        long networkOfferingId = event.getOfferingId();
        long nicId = 0;
        try{
            nicId = Long.parseLong(event.getResourceName());
        }catch (Exception e) {
            s_logger.warn("failed to get nic id from resource name, resource name is: " + event.getResourceName());
        }

        if (EventTypes.EVENT_NETWORK_OFFERING_CREATE.equals(event.getType()) || EventTypes.EVENT_NETWORK_OFFERING_ASSIGN.equals(event.getType())) {
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("Creating networking offering: "+ networkOfferingId +" for Vm: " + vmId + " for account: " + event.getAccountId());
            }
            zoneId = event.getZoneId();
            Account acct = m_accountDao.findByIdIncludingRemoved(event.getAccountId());
            boolean isDefault = (event.getSize() == 1) ? true : false ;
            UsageNetworkOfferingVO networkOffering = new UsageNetworkOfferingVO(zoneId, event.getAccountId(), acct.getDomainId(), vmId, networkOfferingId, nicId, isDefault, event.getCreateDate(), null);
            m_usageNetworkOfferingDao.persist(networkOffering);
        } else if (EventTypes.EVENT_NETWORK_OFFERING_DELETE.equals(event.getType()) || EventTypes.EVENT_NETWORK_OFFERING_REMOVE.equals(event.getType())) {
            SearchCriteria<UsageNetworkOfferingVO> sc = m_usageNetworkOfferingDao.createSearchCriteria();
            sc.addAnd("accountId", SearchCriteria.Op.EQ, event.getAccountId());
            sc.addAnd("vmInstanceId", SearchCriteria.Op.EQ, vmId);
            sc.addAnd("nicId", SearchCriteria.Op.EQ, nicId);
            sc.addAnd("networkOfferingId", SearchCriteria.Op.EQ, networkOfferingId);
            sc.addAnd("deleted", SearchCriteria.Op.NULL);
            List<UsageNetworkOfferingVO> noVOs = m_usageNetworkOfferingDao.search(sc, null);
            if (noVOs.size() > 1) {
                s_logger.warn("More that one usage entry for networking offering: "+ networkOfferingId +" for Vm: " + vmId+" assigned to account: " + event.getAccountId() + "; marking them all as deleted...");
            }
            for (UsageNetworkOfferingVO noVO : noVOs) {
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("deleting network offering: " + noVO.getNetworkOfferingId() + " from Vm: " + noVO.getVmInstanceId());
                }
                noVO.setDeleted(event.getCreateDate()); // there really shouldn't be more than one
                m_usageNetworkOfferingDao.update(noVO);
            }
        }
    }
    
    private void createVPNUserEvent(UsageEventVO event) {

        long zoneId = 0L;

        long userId = event.getResourceId();

        if (EventTypes.EVENT_VPN_USER_ADD.equals(event.getType())) {
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("Creating VPN user: "+ userId + " for account: " + event.getAccountId());
            }
            Account acct = m_accountDao.findByIdIncludingRemoved(event.getAccountId());
            String userName = event.getResourceName();
            UsageVPNUserVO vpnUser = new UsageVPNUserVO(zoneId, event.getAccountId(), acct.getDomainId(), userId, userName, event.getCreateDate(), null);
            m_usageVPNUserDao.persist(vpnUser);
        } else if (EventTypes.EVENT_VPN_USER_REMOVE.equals(event.getType())) {
            SearchCriteria<UsageVPNUserVO> sc = m_usageVPNUserDao.createSearchCriteria();
            sc.addAnd("accountId", SearchCriteria.Op.EQ, event.getAccountId());
            sc.addAnd("userId", SearchCriteria.Op.EQ, userId);
            sc.addAnd("deleted", SearchCriteria.Op.NULL);
            List<UsageVPNUserVO> vuVOs = m_usageVPNUserDao.search(sc, null);
            if (vuVOs.size() > 1) {
                s_logger.warn("More that one usage entry for vpn user: "+ userId +" assigned to account: " + event.getAccountId() + "; marking them all as deleted...");
            }
            for (UsageVPNUserVO vuVO : vuVOs) {
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("deleting vpn user: " + vuVO.getUserId());
                }
                vuVO.setDeleted(event.getCreateDate()); // there really shouldn't be more than one
                m_usageVPNUserDao.update(vuVO);
            }
        }
    }

    private void createSecurityGroupEvent(UsageEventVO event) {

        long zoneId = -1L;

        long vmId = event.getResourceId();
        long sgId = event.getOfferingId();

        if (EventTypes.EVENT_SECURITY_GROUP_ASSIGN.equals(event.getType())) {
            if (s_logger.isDebugEnabled()) {
                s_logger.debug("Assigning : security group"+ sgId +" to Vm: " + vmId + " for account: " + event.getAccountId());
            }
            zoneId = event.getZoneId();
            Account acct = m_accountDao.findByIdIncludingRemoved(event.getAccountId());
            UsageSecurityGroupVO securityGroup = new UsageSecurityGroupVO(zoneId, event.getAccountId(), acct.getDomainId(), vmId, sgId,event.getCreateDate(), null);
            m_usageSecurityGroupDao.persist(securityGroup);
        } else if (EventTypes.EVENT_SECURITY_GROUP_REMOVE.equals(event.getType())) {
            SearchCriteria<UsageSecurityGroupVO> sc = m_usageSecurityGroupDao.createSearchCriteria();
            sc.addAnd("accountId", SearchCriteria.Op.EQ, event.getAccountId());
            sc.addAnd("vmInstanceId", SearchCriteria.Op.EQ, vmId);
            sc.addAnd("securityGroupId", SearchCriteria.Op.EQ, sgId);
            sc.addAnd("deleted", SearchCriteria.Op.NULL);
            List<UsageSecurityGroupVO> sgVOs = m_usageSecurityGroupDao.search(sc, null);
            if (sgVOs.size() > 1) {
                s_logger.warn("More that one usage entry for security group: "+ sgId +" for Vm: " + vmId+" assigned to account: " + event.getAccountId() + "; marking them all as deleted...");
            }
            for (UsageSecurityGroupVO sgVO : sgVOs) {
                if (s_logger.isDebugEnabled()) {
                    s_logger.debug("deleting security group: " + sgVO.getSecurityGroupId() + " from Vm: " + sgVO.getVmInstanceId());
                }
                sgVO.setDeleted(event.getCreateDate()); // there really shouldn't be more than one
                m_usageSecurityGroupDao.update(sgVO);
            }
        }
    }

    private void createVMSnapshotEvent(UsageEventVO event){
        Long vmId = event.getResourceId();
        Long volumeId = event.getTemplateId();
        Long offeringId = event.getOfferingId();
        Long zoneId = event.getZoneId();
        Long accountId = event.getAccountId();
        long size = event.getSize();
        Date created = event.getCreateDate();
        Account acct = m_accountDao.findByIdIncludingRemoved(event.getAccountId());
        Long domainId = acct.getDomainId();
        UsageVMSnapshotVO vsVO  = new UsageVMSnapshotVO(volumeId,zoneId,accountId,
                domainId,vmId,offeringId, size, created, null);
        m_usageVMSnapshotDao.persist(vsVO);
    }

    private class Heartbeat extends ManagedContextRunnable {
        @Override
        protected void runInContext() {
            TransactionLegacy usageTxn = TransactionLegacy.open(TransactionLegacy.USAGE_DB);
            try {
                if(!m_heartbeatLock.lock(3)) { // 3 second timeout
                    if(s_logger.isTraceEnabled())
                        s_logger.trace("Heartbeat lock is in use by others, returning true as someone else will take over the job if required");
                    return;
                }

                try {
                    // check for one-off jobs
                    UsageJobVO nextJob = m_usageJobDao.getNextImmediateJob();
                    if (nextJob != null) {
                        if (m_hostname.equals(nextJob.getHost()) && (m_pid == nextJob.getPid().intValue())) {
                            updateJob(nextJob.getId(), null, null, null, UsageJobVO.JOB_SCHEDULED);
                            scheduleParse();
                        }
                    }

                    Long jobId = m_usageJobDao.checkHeartbeat(m_hostname, m_pid, m_aggregationDuration);
                    if (jobId != null) {
                        // if I'm taking over the job...see how long it's been since the last job, and if it's more than the
                        // aggregation range...do a one off job to catch up.  However, only do this if we are more than half
                        // the aggregation range away from executing the next job
                        long now = System.currentTimeMillis();
                        long timeToJob = m_jobExecTime.getTimeInMillis() - now;
                        long timeSinceJob = 0;
                        long aggregationDurationMillis = m_aggregationDuration * 60 * 1000;
                        long lastSuccess = m_usageJobDao.getLastJobSuccessDateMillis();
                        if (lastSuccess > 0) {
                            timeSinceJob = now - lastSuccess;
                        }

                        if ((timeSinceJob > 0) && (timeSinceJob > (aggregationDurationMillis - 100))) {
                            if (timeToJob > (aggregationDurationMillis/2)) {
                                if (s_logger.isDebugEnabled()) {
                                    s_logger.debug("it's been " + timeSinceJob + " ms since last usage job and " + timeToJob + " ms until next job, scheduling an immediate job to catch up (aggregation duration is " + m_aggregationDuration + " minutes)");
                                }
                                scheduleParse();
                            }
                        }

                        boolean changeOwner = updateJob(jobId, m_hostname, Integer.valueOf(m_pid), new Date(), UsageJobVO.JOB_NOT_SCHEDULED);
                        if (changeOwner) {
                            deleteOneOffJobs(m_hostname, m_pid);
                        }
                    }
                } finally {
                    m_heartbeatLock.unlock();
                }
            } catch (Exception ex) {
                s_logger.error("error in heartbeat", ex);
            } finally {
                usageTxn.close();
            }
        }

        @DB
        protected boolean updateJob(Long jobId, String hostname, Integer pid, Date heartbeat, int scheduled) {
            boolean changeOwner = false;
            TransactionLegacy txn = TransactionLegacy.currentTxn();
            try {
                txn.start();

                // take over the job, setting our hostname/pid/heartbeat time
                UsageJobVO job = m_usageJobDao.lockRow(jobId, Boolean.TRUE);
                if (!job.getHost().equals(hostname) || !job.getPid().equals(pid)) {
                    changeOwner = true;
                }

                UsageJobVO jobForUpdate = m_usageJobDao.createForUpdate();
                if (hostname != null) {
                    jobForUpdate.setHost(hostname);
                }
                if (pid != null) {
                    jobForUpdate.setPid(pid);
                }
                if (heartbeat != null) {
                    jobForUpdate.setHeartbeat(heartbeat);
                }
                jobForUpdate.setScheduled(scheduled);
                m_usageJobDao.update(job.getId(), jobForUpdate);

                txn.commit();
            } catch (Exception dbEx) {
                txn.rollback();
                s_logger.error("error updating usage job", dbEx);
            }
            return changeOwner;
        }

        @DB
        protected void deleteOneOffJobs(String hostname, int pid) {
            SearchCriteria<UsageJobVO> sc = m_usageJobDao.createSearchCriteria();
            SearchCriteria<UsageJobVO> ssc = m_usageJobDao.createSearchCriteria();
            ssc.addOr("host", SearchCriteria.Op.NEQ, hostname);
            ssc.addOr("pid", SearchCriteria.Op.NEQ, pid);
            sc.addAnd("host", SearchCriteria.Op.SC, ssc);
            sc.addAnd("endMillis", SearchCriteria.Op.EQ, Long.valueOf(0));
            sc.addAnd("jobType", SearchCriteria.Op.EQ, Integer.valueOf(UsageJobVO.JOB_TYPE_SINGLE));
            sc.addAnd("scheduled", SearchCriteria.Op.EQ, Integer.valueOf(0));
            m_usageJobDao.expunge(sc);
        }
    }
    
    private class SanityCheck extends ManagedContextRunnable {
        @Override
        protected void runInContext() {
            UsageSanityChecker usc = new UsageSanityChecker();
            try {
                String errors = usc.runSanityCheck();
                if(errors.length() > 0){
                   _alertMgr.sendAlert(AlertManager.AlertType.ALERT_TYPE_USAGE_SANITY_RESULT, 0, new Long(0), "Usage Sanity Check failed", errors);
                } else {
                    _alertMgr.clearAlert(AlertManager.AlertType.ALERT_TYPE_USAGE_SANITY_RESULT, 0, 0);
                }
            } catch (SQLException e) {
                s_logger.error("Error in sanity check", e);
            }
        }
    }
}
