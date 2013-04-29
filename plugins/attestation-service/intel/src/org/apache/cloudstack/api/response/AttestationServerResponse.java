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
package org.apache.cloudstack.api.response;

import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.EntityReference;
import org.apache.cloudstack.api.BaseResponse;
import org.apache.cloudstack.database.AttestationServerVO;
import com.cloud.serializer.Param;
import com.google.gson.annotations.SerializedName;

@EntityReference(value=AttestationServerVO.class)
public class AttestationServerResponse extends BaseResponse {
    @SerializedName(ApiConstants.ID) @Param(description="the ID of the attestation server")
    private String id;

    @SerializedName(ApiConstants.URL) @Param(description="the url of the attestation server")
    private String url;

    @SerializedName(ApiConstants.USERNAME) @Param(description="the username with which to connect to the attestation server")
    private String username;

    @SerializedName(ApiConstants.ZONE_ID) @Param(description="the zone with which the attestation server is registered")
    private String zoneid;

    @SerializedName(ApiConstants.NAME) @Param(description="name to identify the attestation server")
    private String name;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getUsername() {
        return username;
    }

    public void setUsername(String username) {
        this.username = username;
    }

    public String getZoneId() {
        return zoneid;
    }

    public void setZoneId(String zoneid) {
        this.zoneid = zoneid;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }
}
