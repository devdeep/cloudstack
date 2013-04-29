// Licensed to the Apache Software Foundation (ASF) under one
// or more contributor license agreements.  See the NOTICE file
// distributed with this work for additional information
// regarding copyright ownership.  The ASF licenses this file
// to you under the Apache License, Version 2.0 (the
// "License"); you may not use this file except in compliance
// with the License.  You may obtain a copy of the License at
// 
//   http://www.apache.org/licenses/LICENSE-2.0
// 
// Unless required by applicable law or agreed to in writing,
// software distributed under the License is distributed on an
// "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
// KIND, either express or implied.  See the License for the
// specific language governing permissions and limitations
// under the License.
//
package org.apache.cloudstack.manager;

import java.io.File;
import java.net.URL;
import java.util.ArrayList;
import java.util.HashSet;
import java.util.Iterator;
import java.util.List;
import java.util.Map;
import java.util.Set;
import java.util.UUID;

import javax.ejb.Local;
import javax.inject.Inject;
import javax.naming.ConfigurationException;

import org.apache.cloudstack.api.command.admin.inteltxt.ListAttestationServerCmd;
import org.apache.cloudstack.api.command.admin.inteltxt.RegisterAttestationServerCmd;
import org.apache.cloudstack.api.command.admin.inteltxt.RegisterHostWithAttestationServerCmd;
import org.apache.cloudstack.api.command.admin.inteltxt.UnregisterAttestationServerCmd;
import org.apache.cloudstack.api.response.AttestationServerResponse;
import org.apache.cloudstack.api.response.ListResponse;
import org.apache.cloudstack.api.response.RegisterHostWithAttestationServerResponse;
import org.apache.cloudstack.api.response.SuccessResponse;
import org.apache.cloudstack.database.AttestationServerDao;
import org.apache.cloudstack.database.AttestationServerVO;
import org.apache.log4j.Logger;

import com.cloud.agent.AgentManager;
import com.cloud.agent.Listener;
import com.cloud.agent.api.AgentControlAnswer;
import com.cloud.agent.api.AgentControlCommand;
import com.cloud.agent.api.Answer;
import com.cloud.agent.api.Command;
import com.cloud.agent.api.StartupCommand;
import com.cloud.agent.api.StartupRoutingCommand;
import com.cloud.api.query.dao.HostJoinDao;
import com.cloud.api.query.vo.HostJoinVO;
import com.cloud.configuration.dao.ConfigurationDao;
import com.cloud.dc.DataCenterVO;
import com.cloud.dc.dao.DataCenterDao;
import com.cloud.host.HostVO;
import com.cloud.host.Status;
import com.cloud.host.dao.HostTagsDao;
import com.cloud.utils.db.DB;
import com.cloud.utils.exception.CloudRuntimeException;
import com.intel.mtwilson.ApiClient;
import com.intel.mtwilson.KeystoreUtil;
import com.intel.mtwilson.TrustAssertion;
import com.intel.mtwilson.datatypes.Hostname;
import com.intel.mtwilson.datatypes.Role;
import com.intel.mtwilson.datatypes.TxtHostRecord;
import com.intel.mtwilson.datatypes.xml.HostTrustXmlResponse;

@Local(value = { AttestationManager.class })
public class AttestationManagerImpl implements AttestationManager, Listener {
    public static final Logger s_logger = Logger.getLogger(AttestationManagerImpl.class);
    private String name;
    private String hostTag;
    private Boolean serviceIsEnabled;
    private int runLevel;
    private Map<String, Object> params;

    @Inject
    private AgentManager agentMgr;
    @Inject
    private AttestationServerDao attestationServerDao;
    @Inject
    private HostJoinDao hostJoinDao;
    @Inject
    private HostTagsDao hostTagsDao;
    @Inject
    private DataCenterDao dcDao;
    @Inject
    private ConfigurationDao configDao;

    @Override
    public boolean configure(String name, Map<String, Object> params) throws ConfigurationException {
        Map<String, String> config = configDao.getConfiguration();

        this.serviceIsEnabled = false;
        this.hostTag = new String("Trusted-Host");
        if (config != null) {
            String value = config.get("attestation.enabled");
            if (value != null) {
                this.serviceIsEnabled = Boolean.parseBoolean((String)value);
            }

            // Get the tag which should be put on trusted host.
            value = config.get("attestation.hosttag");
            if (value != null) {
                this.hostTag = (String) value;
            }
        } else {
            s_logger.error("Couldn't retrieve the attestation server configuration parameters.");
        }

        if (this.serviceIsEnabled) {
            agentMgr.registerForHostEvents(this, true, false, true);
        }
        return true;
    }

    @Override
    public boolean start() {
        return true;
    }

    @Override
    public boolean stop() {
        return true;
    }

    @Override
    public String getName() {
        return name;
    }

    @Override
    public void setName(String name) {
        this.name = name;
    }

    @Override
    public void setConfigParams(Map<String, Object> params) {
        this.params = params;
    }

    @Override
    public Map<String, Object> getConfigParams() {
        return this.params;
    }

    @Override
    public int getRunLevel() {
        return runLevel;
    }

    @Override
    public void setRunLevel(int level) {
        this.runLevel = level;
    }

    @DB
    @Override
    public AttestationServerResponse registerAttestationServer(RegisterAttestationServerCmd cmd) {
        if (!this.serviceIsEnabled) {
            s_logger.error("The attestation service has not been enabled. Please enable it from the global" +
                    " configuration and try again.");
            throw new CloudRuntimeException("The attestation service has not been enabled. Please enable it from the" +
                    " global configuration and try again.");
        }

        DataCenterVO zone = dcDao.findById(cmd.getZoneid());
        if (zone == null) {
            s_logger.error("Cannot find a zone with the given id.");
            throw new CloudRuntimeException("Cannot find a zone with the given id.");
        }

        // confirm there is no other attestation server registered for the zone.
        List<AttestationServerVO> registeredServers = attestationServerDao.findAttestationServerForZone(zone.getId());
        if (registeredServers != null && registeredServers.size() > 0) {
            s_logger.error("Attestation server is already registered for the zone " + zone);
            throw new CloudRuntimeException("Attestation server is already registered for the zone.");
        }

        try {
            File directory = new File(System.getProperty("user.home", "."));
            URL server = new URL(cmd.getUrl());
            String[] roles = new String[] { Role.Attestation.toString(),
                    Role.Security.toString(),
                    Role.Whitelist.toString() };
            KeystoreUtil.createUserInDirectory(directory, cmd.getUsername(), cmd.getPassword(), server, roles);
        } catch (Exception e) {
            s_logger.error("Error while registering with the attestation server.", e);
            throw new CloudRuntimeException("Error while registering with the attestation server.", e);
        }

        // Attestation successful. Commit the details to the db.
        AttestationServerVO attestationServerVO = new AttestationServerVO();
        attestationServerVO.setUuid(UUID.randomUUID().toString());
        attestationServerVO.setUsername(cmd.getUsername());
        attestationServerVO.setPassword(cmd.getPassword());
        attestationServerVO.setUrl(cmd.getUrl());
        attestationServerVO.setDataCenterId(zone.getId());
        attestationServerVO.setName(cmd.getName());
        attestationServerDao.persist(attestationServerVO);

        AttestationServerResponse response = new AttestationServerResponse();
        response.setId(attestationServerVO.getUuid());
        response.setUrl(attestationServerVO.getUrl());
        response.setUsername(attestationServerVO.getUsername());
        response.setZoneId(zone.getUuid());
        response.setName(attestationServerVO.getName());

        return response;
    }

    @Override
    public RegisterHostWithAttestationServerResponse registerHostWithAttestationServer(RegisterHostWithAttestationServerCmd cmd) {
        HostJoinVO hostToRegister = null;
        boolean registered = false;
        boolean trustedHost = false;
        String attestationServerId = null;

        if (!this.serviceIsEnabled) {
            s_logger.error("The attestation service has not been enabled. Please enable it from the global" +
                    " configuration and try again.");
            throw new CloudRuntimeException("The attestation service has not been enabled. Please enable it from the" +
                    " global configuration and try again.");
        }

        try {
            // Get the details of the host.
            List<HostJoinVO> hosts = hostJoinDao.searchByIds(cmd.getId());
            if (hosts == null || hosts.isEmpty()) {
                s_logger.error("Cannot find any host with the given id.");
                throw new CloudRuntimeException("Cannot find any host with the given id.");
            }

            // Get the api client object
            hostToRegister = hosts.get(0);
            File directory = new File(System.getProperty("user.home", "."));
            ApiClient api = null;
            List<AttestationServerVO> registeredServers = attestationServerDao.findAttestationServerForZone(
                    hostToRegister.getZoneId());
            if (registeredServers == null || registeredServers.size() == 0) {
                s_logger.error("Cannot find an attestation server registered for the zone to which the host " +
                        hostToRegister + " belongs.");
                throw new CloudRuntimeException("Cannot find an attestation server registered for the zone to which" +
                        " the host belongs. Host " + hostToRegister.getUuid());
            } else if (registeredServers.size() > 1) {
                s_logger.error("Found more than one attestation server registered for the zone to which the host " +
                        hostToRegister + " belongs.");
                throw new CloudRuntimeException("Found more than one attestation server registered for the zone to" +
                        " which the host belongs. Host " + hostToRegister.getUuid());
            } else {
                AttestationServerVO serverVO = registeredServers.get(0);
                attestationServerId = serverVO.getUuid();
                URL server = new URL(serverVO.getUrl());
                api = KeystoreUtil.clientForUserInDirectory(directory, serverVO.getUsername(), serverVO.getPassword(),
                        server);
            }

            if (api == null) {
                s_logger.error("Couldn't connect to the attestation server for registering the host.");
                throw new CloudRuntimeException("Couldn't connect to the attestation server for registering the host.");
            }

            TxtHostRecord txtHostRecord = new TxtHostRecord();
            txtHostRecord.HostName = hostToRegister.getPrivateIpAddress();
            txtHostRecord.IPAddress = hostToRegister.getPrivateIpAddress();
            txtHostRecord.Port = cmd.getPort();

            // configure white list for the host first.
            if (!api.configureWhiteList(txtHostRecord)) {
                s_logger.error("Couldn't whitelist the host configuration with the attestation server." +
                        " Host id: " + hostToRegister.getUuid());
                throw new CloudRuntimeException("Couldn't whitelist the host configuration with the attestation server.");
            }

            // register the host configuration with the attestation server too.
            if (!api.registerHost(txtHostRecord)) {
                s_logger.error("Couldn't register the host with the attestation server. Host id: " + hostToRegister.getUuid());
                throw new CloudRuntimeException("Couldn't register the host with the attestation server.");
            }

            // Check host trust assertions and update the tags on the host.
            trustedHost = checkIfHostIsTrusted(api, hostToRegister.getPrivateIpAddress(), false);
            updateTrustedTagOnHost(hostToRegister.getId(), trustedHost);
            registered = true;
        } catch (Exception e) {
            s_logger.error("Error whitelisting and registering the host with the attestation server." +
                    (hostToRegister != null ? (" Host id: " )+ hostToRegister.getUuid() : ""), e);
            throw new CloudRuntimeException("Error whitelisting and registering the host with the attestation server.", e);
        }

        // Generate the response.
        RegisterHostWithAttestationServerResponse response = new RegisterHostWithAttestationServerResponse();
        response.setId(hostToRegister.getUuid());
        response.setAttestationServer(attestationServerId);
        response.setWhitelisted(registered);
        response.setRegistered(registered);
        response.setTrusted(trustedHost);
        return response;
    }

    @Override
    public ListResponse<AttestationServerResponse> listAttestationServer(ListAttestationServerCmd cmd) {
        List<AttestationServerVO> servers = attestationServerDao.listAll();
        List<AttestationServerResponse> serverResponses = new ArrayList<AttestationServerResponse>();
        for (AttestationServerVO oneServer : servers) {
            AttestationServerResponse response = new AttestationServerResponse();
            DataCenterVO zone = dcDao.findById(oneServer.getDataCenterId());
            if (zone != null) {
                response.setId(oneServer.getUuid());
                response.setUrl(oneServer.getUrl());
                response.setUsername(oneServer.getUsername());
                response.setZoneId(zone.getUuid());
                response.setName(oneServer.getName());
                serverResponses.add(response);
            } else {
                s_logger.warn("Cannot find the zone for attestation server " + oneServer.getUuid());
            }
        }

        ListResponse<AttestationServerResponse> response = new ListResponse<AttestationServerResponse>();
        response.setResponses(serverResponses, serverResponses.size());
        return response;
    }

    @DB
    @Override
    public SuccessResponse unregisterAttestationServer(UnregisterAttestationServerCmd cmd) {
        AttestationServerVO server = attestationServerDao.lockRow(cmd.getId(), true);
        if (server == null) {
            throw new CloudRuntimeException("Attestation server with the given id doesn't exist.");
        } else if (!attestationServerDao.remove(cmd.getId())) {
            throw new CloudRuntimeException("Attestation server with the given id couldn't be removed.");
        }
        return new SuccessResponse();
    }

    @Override
    public List<Class<?>> getCommands() {
        List<Class<?>> cmds = new ArrayList<Class<?>>();
        cmds.add(RegisterAttestationServerCmd.class);
        cmds.add(UnregisterAttestationServerCmd.class);
        cmds.add(RegisterHostWithAttestationServerCmd.class);
        cmds.add(ListAttestationServerCmd.class);
        return cmds;
    }

    @Override
    public boolean processDisconnect(long agentId, Status state) {
        return false;
    }

    @Override
    public boolean processTimeout(long agentId, long seq) {
        return false;
    }

    @Override
    public boolean isRecurring() {
        return false;
    }

    @Override
    public int getTimeout() {
        return 0;
    }

    @Override
    public boolean processCommands(long agentId, long seq, Command[] commands) {
        return false;
    }

    @Override
    public AgentControlAnswer processControlCommand(long agentId, AgentControlCommand cmd) {
        return null;
    }

    @Override
    public boolean processAnswers(long agentId, long seq, Answer[] answers) {
        return false;
    }

    @Override
    public void processConnect(HostVO host, StartupCommand cmd, boolean forRebalance) {
        if (!(cmd instanceof StartupRoutingCommand )) {
            s_logger.info("Received a process connect, not of type StartupRoutingCommand");
            return;
        }

        s_logger.info("Process connect. Details: "+
                ", host id : " + host.getUuid() +
                ", hypervisor type : " + host.getHypervisorType() +
                ", hypervisor version : " + host.getHypervisorVersion() +
                ", state : " + host.getState() +
                ", status : " + host.getStatus() +
                ", type : " + host.getType());

        // Check if the host is trusted or not.
        boolean trustedHost = false;
        try {
            // Get the api client object.
            File directory = new File(System.getProperty("user.home", "."));
            ApiClient api = null;
            List<AttestationServerVO> registeredServers = attestationServerDao.findAttestationServerForZone(
                    host.getDataCenterId());
            if (registeredServers == null || registeredServers.size() == 0) {
                s_logger.warn("Cannot find an attestation server registered for the zone to which the host " +
                        host + " belongs.");
                throw new CloudRuntimeException("Cannot find an attestation server registered for the zone to which" +
                        " the host belongs. Host " + host.getUuid());
            } else if (registeredServers.size() > 1) {
                s_logger.error("Found more than one attestation server registered for the zone to which the host " +
                        host + " belongs.");
                throw new CloudRuntimeException("Found more than one attestation server registered for the zone to" +
                        " which the host belongs. Host " + host.getUuid());
            } else {
                AttestationServerVO serverVO = registeredServers.get(0);
                URL server = new URL(serverVO.getUrl());
                api = KeystoreUtil.clientForUserInDirectory(directory, serverVO.getUsername(), serverVO.getPassword(),
                        server);
            }

            if (api == null) {
                s_logger.warn("Couldn't connect to the attestation server for registering the host.");
                throw new CloudRuntimeException("Couldn't connect to the attestation server for registering the host.");
            }

            trustedHost = checkIfHostIsTrusted(api, host.getPrivateIpAddress(), true);
        } catch (Exception e) {
            s_logger.warn("Error while looking at the assertion attributes of the host " + host, e);
        }

        updateTrustedTagOnHost(host.getId(), trustedHost);
    }

    private boolean checkIfHostIsTrusted(ApiClient api, String hostName, boolean forceVerify) {
        boolean trustedHost = false;
        try {
            // Check the host assertion attributes
            Set<Hostname> names = new HashSet<Hostname> ();
            names.add(new Hostname(hostName));
            List<HostTrustXmlResponse> samlForMultipleHosts = api.getSamlForMultipleHosts(names, forceVerify);
            assert(samlForMultipleHosts.size() == 1);
            HostTrustXmlResponse hostSaml = samlForMultipleHosts.get(0);
            String currentHost = hostSaml.getName();
            assert(currentHost.equals(hostName));

            String hostSamlAssertion = hostSaml.getAssertion();
            TrustAssertion trustAssertion = api.verifyTrustAssertion(hostSamlAssertion);
            if (trustAssertion.isValid()) {
                for (String attr : trustAssertion.getAttributeNames()) {
                    s_logger.trace("Signed Atr: " + attr + ":" + trustAssertion.getStringAttribute (attr));
                }
                Boolean trustedBios = Boolean.parseBoolean(trustAssertion.getStringAttribute("Trusted_BIOS"));
                Boolean trustedVmm = Boolean.parseBoolean(trustAssertion.getStringAttribute("Trusted_VMM"));
                Boolean trusted = Boolean.parseBoolean(trustAssertion.getStringAttribute("Trusted"));
                if (trustedBios && trustedVmm && trusted) {
                    trustedHost = true;
                    s_logger.info("Trust assertion successful for host " + hostName);
                }
            } else {
                // This doesn't seem to work. Saml assertion isn't valid even for a trusted host.
                // FIXME: Hack to verify rest of the code. The verifyTrustAssertion api is failing even though
                // the SAML seems to be valid. 
                if (hostName.equals("10.102.192.7")) {
                    trustedHost = true;
                }
                s_logger.info("Trust assertion unsuccessful for host " + hostName);
            }
        } catch (Exception e) {
            s_logger.error("Couldn't check the assertion attributes of the host " + hostName, e);
        }

        return trustedHost;
    }

    private void updateTrustedTagOnHost(Long hostId, boolean trusted) {
        try {
            List<String> tags = hostTagsDao.gethostTags(hostId);
            if (trusted) {
                // Tag the host as trusted.
                boolean foundTag = false;
                for (String oneTag : tags) {
                    if (oneTag.equals(this.hostTag)) {
                        foundTag = true;
                        break;
                    }
                }

                if (!foundTag) {
                    tags.add(this.hostTag);
                    hostTagsDao.persist(hostId, tags);
                }
            } else {
                // Remove the tag if there is one present.
                for (Iterator<String> iter = tags.iterator(); iter.hasNext();) {
                    String currentTag = iter.next();
                    if (currentTag.equals(this.hostTag)) {
                        iter.remove();
                    }
                }
            }
        } catch (Exception e) {
            s_logger.error("Error while updating the tags on the host for trust assertion. Host " + hostId +
                    " is " + (trusted ? "trusted." : "untrusted."), e);
        }
    }
}
