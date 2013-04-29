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
import org.apache.cloudstack.api.BaseResponse;

import com.cloud.serializer.Param;
import com.google.gson.annotations.SerializedName;

public class RegisterHostWithAttestationServerResponse extends BaseResponse {
    @SerializedName(ApiConstants.ID) @Param(description="the ID of the host that was registered with the attestation server")
    private String id;

    @SerializedName("attestationserver") @Param(description="the ID of the attestation server with which the host was registered")
    private String attestationServer;

    @SerializedName("whitelisted") @Param(description="the host was whitelisted with the attestaion server")
    private Boolean whitelisted;

    @SerializedName("registered") @Param(description="the host was registered with the attestaion server")
    private Boolean registered;

    @SerializedName("trusted") @Param(description="trust assertions of the host were establisged with the attestaion server")
    private Boolean trusted;

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getAttestationServer() {
        return attestationServer;
    }

    public void setAttestationServer(String attestationServer) {
        this.attestationServer = attestationServer;
    }

    public Boolean getWhitelisted() {
        return whitelisted;
    }

    public void setWhitelisted(Boolean whitelisted) {
        this.whitelisted = whitelisted;
    }

    public Boolean getRegistered() {
        return registered;
    }

    public void setRegistered(Boolean registered) {
        this.registered = registered;
    }
    
    public Boolean getTrusted() {
        return trusted;
    }

    public void setTrusted(Boolean trusted) {
        this.trusted = trusted;
    }
}
