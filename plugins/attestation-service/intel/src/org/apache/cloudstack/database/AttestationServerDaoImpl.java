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
package org.apache.cloudstack.database;

import java.util.List;

import javax.ejb.Local;

import com.cloud.utils.db.DB;
import com.cloud.utils.db.GenericDaoBase;
import com.cloud.utils.db.SearchBuilder;
import com.cloud.utils.db.SearchCriteria;

@Local(value = { AttestationServerDao.class })
@DB(txn = false)
public class AttestationServerDaoImpl  extends GenericDaoBase<AttestationServerVO, Long> implements AttestationServerDao {
    protected SearchBuilder<AttestationServerVO> ListAttestationServerForZoneSearch;

    @Override
    public List<AttestationServerVO> findAttestationServerForZone(Long zoneId) {
        SearchCriteria<AttestationServerVO> sc = ListAttestationServerForZoneSearch.create();
        sc.setParameters("zoneId", zoneId);
        return listBy(sc);
    }

    public AttestationServerDaoImpl() {
        ListAttestationServerForZoneSearch = createSearchBuilder();
        ListAttestationServerForZoneSearch.and("zoneId", ListAttestationServerForZoneSearch.entity().getDataCenterId(),
                SearchCriteria.Op.EQ);
        ListAttestationServerForZoneSearch.and("removed", ListAttestationServerForZoneSearch.entity().getRemoved(),
                SearchCriteria.Op.NULL);
        ListAttestationServerForZoneSearch.done();
    }
}
