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
package com.cloud.dc.dao;

import java.util.List;

import javax.ejb.Local;

import org.springframework.stereotype.Component;

import com.cloud.dc.DedicatedResourceVO;
import com.cloud.user.Account;
import com.cloud.user.UserContext;
import com.cloud.utils.db.DB;
import com.cloud.utils.db.GenericDaoBase;
import com.cloud.utils.db.SearchBuilder;
import com.cloud.utils.db.SearchCriteria;
import com.cloud.utils.db.Transaction;

@Component
@Local(value={DedicatedResourceDao.class}) @DB(txn = false)
public class DedicatedResourceDaoImpl extends GenericDaoBase<DedicatedResourceVO, Long> implements DedicatedResourceDao {
    protected final SearchBuilder<DedicatedResourceVO> ZoneSearch;
    protected final SearchBuilder<DedicatedResourceVO> PodSearch;
    protected final SearchBuilder<DedicatedResourceVO> ClusterSearch;
    protected final SearchBuilder<DedicatedResourceVO> HostSearch;

    protected SearchBuilder<DedicatedResourceVO> ListZonesByDomainIdSearch;
    protected SearchBuilder<DedicatedResourceVO> ListPodsByDomainIdSearch;
    protected SearchBuilder<DedicatedResourceVO> ListClustersByDomainIdSearch;
    protected SearchBuilder<DedicatedResourceVO> ListHostsByDomainIdSearch;

    protected SearchBuilder<DedicatedResourceVO> ListZonesByAccountIdSearch;
    protected SearchBuilder<DedicatedResourceVO> ListPodsByAccountIdSearch;
    protected SearchBuilder<DedicatedResourceVO> ListClustersByAccountIdSearch;
    protected SearchBuilder<DedicatedResourceVO> ListHostsByAccountIdSearch;

    protected SearchBuilder<DedicatedResourceVO> ListAllZones;
    protected SearchBuilder<DedicatedResourceVO> ListAllPods;
    protected SearchBuilder<DedicatedResourceVO> ListAllClusters;
    protected SearchBuilder<DedicatedResourceVO> ListAllHosts;

    protected SearchBuilder<DedicatedResourceVO> ListByAccountId;
    protected SearchBuilder<DedicatedResourceVO> ListByDomainId;

    protected SearchBuilder<DedicatedResourceVO> ZoneByDomainIdsSearch;

    protected DedicatedResourceDaoImpl() {
        PodSearch = createSearchBuilder();
        PodSearch.and("podId", PodSearch.entity().getPodId(), SearchCriteria.Op.EQ);
        PodSearch.done();

        ZoneSearch = createSearchBuilder();
        ZoneSearch.and("zoneId", ZoneSearch.entity().getDataCenterId(), SearchCriteria.Op.EQ);
        ZoneSearch.done();

        ClusterSearch = createSearchBuilder();
        ClusterSearch.and("clusterId", ClusterSearch.entity().getClusterId(), SearchCriteria.Op.EQ);
        ClusterSearch.done();

        HostSearch = createSearchBuilder();
        HostSearch.and("hostId", HostSearch.entity().getHostId(), SearchCriteria.Op.EQ);
        HostSearch.done();

        ListZonesByDomainIdSearch = createSearchBuilder();
        ListZonesByDomainIdSearch.and("zoneId", ListZonesByDomainIdSearch.entity().getDataCenterId(), SearchCriteria.Op.NNULL);
        ListZonesByDomainIdSearch.and("domainId", ListZonesByDomainIdSearch.entity().getDomainId(), SearchCriteria.Op.EQ);
        ListZonesByDomainIdSearch.and("accountId", ListZonesByDomainIdSearch.entity().getAccountId(), SearchCriteria.Op.NULL);
        ListZonesByDomainIdSearch.done();

        ListZonesByAccountIdSearch = createSearchBuilder();
        ListZonesByAccountIdSearch.and("zoneId", ListZonesByAccountIdSearch.entity().getDataCenterId(), SearchCriteria.Op.NNULL);
        ListZonesByAccountIdSearch.and("accountId", ListZonesByAccountIdSearch.entity().getAccountId(), SearchCriteria.Op.EQ);
        ListZonesByAccountIdSearch.done();

        ListPodsByDomainIdSearch = createSearchBuilder();
        ListPodsByDomainIdSearch.and("podId", ListPodsByDomainIdSearch.entity().getPodId(), SearchCriteria.Op.NNULL);
        ListPodsByDomainIdSearch.and("domainId", ListPodsByDomainIdSearch.entity().getDomainId(), SearchCriteria.Op.EQ);
        ListPodsByDomainIdSearch.and("accountId", ListPodsByDomainIdSearch.entity().getAccountId(), SearchCriteria.Op.NULL);
        ListPodsByDomainIdSearch.done();

        ListPodsByAccountIdSearch = createSearchBuilder();
        ListPodsByAccountIdSearch.and("podId", ListPodsByAccountIdSearch.entity().getPodId(), SearchCriteria.Op.NNULL);
        ListPodsByAccountIdSearch.and("accountId", ListPodsByAccountIdSearch.entity().getAccountId(), SearchCriteria.Op.EQ);
        ListPodsByAccountIdSearch.done();

        ListClustersByDomainIdSearch = createSearchBuilder();
        ListClustersByDomainIdSearch.and("clusterId", ListClustersByDomainIdSearch.entity().getClusterId(), SearchCriteria.Op.NNULL);
        ListClustersByDomainIdSearch.and("domainId", ListClustersByDomainIdSearch.entity().getDomainId(), SearchCriteria.Op.EQ);
        ListClustersByDomainIdSearch.and("accountId", ListClustersByDomainIdSearch.entity().getAccountId(), SearchCriteria.Op.NULL);
        ListClustersByDomainIdSearch.done();

        ListClustersByAccountIdSearch = createSearchBuilder();
        ListClustersByAccountIdSearch.and("clusterId", ListClustersByAccountIdSearch.entity().getClusterId(), SearchCriteria.Op.NNULL);
        ListClustersByAccountIdSearch.and("accountId", ListClustersByAccountIdSearch.entity().getAccountId(), SearchCriteria.Op.EQ);
        ListClustersByAccountIdSearch.done();

        ListHostsByDomainIdSearch = createSearchBuilder();
        ListHostsByDomainIdSearch.and("hostId", ListHostsByDomainIdSearch.entity().getHostId(), SearchCriteria.Op.NNULL);
        ListHostsByDomainIdSearch.and("domainId", ListHostsByDomainIdSearch.entity().getDomainId(), SearchCriteria.Op.EQ);
        ListHostsByDomainIdSearch.and("accountId", ListHostsByDomainIdSearch.entity().getAccountId(), SearchCriteria.Op.NULL);
        ListHostsByDomainIdSearch.done();

        ListHostsByAccountIdSearch = createSearchBuilder();
        ListHostsByAccountIdSearch.and("hostId", ListHostsByAccountIdSearch.entity().getHostId(), SearchCriteria.Op.NNULL);
        ListHostsByAccountIdSearch.and("accountId", ListHostsByAccountIdSearch.entity().getAccountId(), SearchCriteria.Op.EQ);
        ListHostsByAccountIdSearch.done();

        ListAllZones = createSearchBuilder();
        ListAllZones.and("zoneId", ListAllZones.entity().getDataCenterId(), SearchCriteria.Op.NNULL);
        ListAllZones.done();

        ListAllPods = createSearchBuilder();
        ListAllPods.and("podId", ListAllPods.entity().getPodId(), SearchCriteria.Op.NNULL);
        ListAllPods.done();

        ListAllClusters = createSearchBuilder();
        ListAllClusters.and("clusterId", ListAllClusters.entity().getClusterId(), SearchCriteria.Op.NNULL);
        ListAllClusters.done();

        ListAllHosts = createSearchBuilder();
        ListAllHosts.and("hostId", ListAllHosts.entity().getHostId(), SearchCriteria.Op.NNULL);
        ListAllHosts.done();

        ListByAccountId = createSearchBuilder();
        ListByAccountId.and("accountId", ListByAccountId.entity().getAccountId(), SearchCriteria.Op.EQ);
        ListByAccountId.done();

        ListByDomainId = createSearchBuilder();
        ListByDomainId.and("accountId", ListByDomainId.entity().getAccountId(), SearchCriteria.Op.NULL);
        ListByDomainId.and("domainId", ListByDomainId.entity().getDomainId(), SearchCriteria.Op.EQ);
        ListByDomainId.done();

        ZoneByDomainIdsSearch = createSearchBuilder();
        ZoneByDomainIdsSearch.and("zoneId", ZoneByDomainIdsSearch.entity().getDataCenterId(), SearchCriteria.Op.NNULL);
        ZoneByDomainIdsSearch.and("domainId", ZoneByDomainIdsSearch.entity().getDomainId(), SearchCriteria.Op.NIN);
        ZoneByDomainIdsSearch.done();
    }

    @Override
    public DedicatedResourceVO findByZoneId(Long zoneId) {
        SearchCriteria<DedicatedResourceVO> sc = ZoneSearch.create();
        sc.setParameters("zoneId", zoneId);
        return findOneBy(sc);
    }

    @Override
    public DedicatedResourceVO findByPodId(Long podId) {
        SearchCriteria<DedicatedResourceVO> sc = PodSearch.create();
        sc.setParameters("podId", podId);

        return findOneBy(sc);
    }

    @Override
    public DedicatedResourceVO findByClusterId(Long clusterId) {
        SearchCriteria<DedicatedResourceVO> sc = ClusterSearch.create();
        sc.setParameters("clusterId", clusterId);

        return findOneBy(sc);
    }

    @Override
    public DedicatedResourceVO findByHostId(Long hostId) {
        SearchCriteria<DedicatedResourceVO> sc = HostSearch.create();
        sc.setParameters("hostId", hostId);

        return findOneBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> listZones(){
        SearchCriteria<DedicatedResourceVO> sc = ListAllZones.create();
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> listPods(){
        SearchCriteria<DedicatedResourceVO> sc = ListAllPods.create();
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> listClusters(){
        SearchCriteria<DedicatedResourceVO> sc = ListAllClusters.create();
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> listHosts(){
        SearchCriteria<DedicatedResourceVO> sc = ListAllHosts.create();
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> listByAccountId(Long accountId){
        SearchCriteria<DedicatedResourceVO> sc = ListByAccountId.create();
        sc.setParameters("accountId", accountId);
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> listByDomainId(Long domainId){
        SearchCriteria<DedicatedResourceVO> sc = ListByDomainId.create();
        sc.setParameters("domainId", domainId);
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> findZonesByDomainId(Long domainId){
        SearchCriteria<DedicatedResourceVO> sc = ListZonesByDomainIdSearch.create();
        sc.setParameters("domainId", domainId);
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> findZonesByAccountId(Long accountId){
        SearchCriteria<DedicatedResourceVO> sc = ListZonesByAccountIdSearch.create();
        sc.setParameters("accountId", accountId);
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> findPodsByDomainId(Long domainId){
        SearchCriteria<DedicatedResourceVO> sc = ListPodsByDomainIdSearch.create();
        sc.setParameters("domainId", domainId);
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> findPodsByAccountId(Long accountId){
        SearchCriteria<DedicatedResourceVO> sc = ListPodsByAccountIdSearch.create();
        sc.setParameters("accountId", accountId);
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> findClustersByDomainId(Long domainId){
        SearchCriteria<DedicatedResourceVO> sc = ListClustersByDomainIdSearch.create();
        sc.setParameters("domainId", domainId);
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> findClustersByAccountId(Long accountId){
        SearchCriteria<DedicatedResourceVO> sc = ListClustersByAccountIdSearch.create();
        sc.setParameters("accountId", accountId);
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> findHostsByDomainId(Long domainId){
        SearchCriteria<DedicatedResourceVO> sc = ListHostsByDomainIdSearch.create();
        sc.setParameters("domainId", domainId);
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> findHostsByAccountId(Long accountId){
        SearchCriteria<DedicatedResourceVO> sc = ListHostsByAccountIdSearch.create();
        sc.setParameters("accountId", accountId);
        return listBy(sc);
    }

    @Override
    public List<DedicatedResourceVO> listZonesNotInDomainIds(List<Long> domainIds) {
        SearchCriteria<DedicatedResourceVO> sc = ZoneByDomainIdsSearch.create();
        sc.setParameters("domainId", domainIds.toArray(new Object[domainIds.size()]));
        return listBy(sc);
    }

    @Override
    public boolean remove(Long id) {
        Transaction txn = Transaction.currentTxn();
        txn.start();
        DedicatedResourceVO resource = createForUpdate();
        update(id, resource);

        boolean result = super.remove(id);
        txn.commit();
        return result;
    }
}
