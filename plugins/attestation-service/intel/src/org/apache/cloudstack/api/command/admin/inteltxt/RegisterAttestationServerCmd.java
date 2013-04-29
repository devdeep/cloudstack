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
package org.apache.cloudstack.api.command.admin.inteltxt;

import javax.inject.Inject;

import org.apache.cloudstack.api.APICommand;
import org.apache.cloudstack.api.ApiConstants;
import org.apache.cloudstack.api.ApiErrorCode;
import org.apache.cloudstack.api.BaseAsyncCmd;
import org.apache.cloudstack.api.Parameter;
import org.apache.cloudstack.api.ServerApiException;
import org.apache.cloudstack.api.response.AttestationServerResponse;
import org.apache.cloudstack.api.response.ZoneResponse;
import org.apache.cloudstack.manager.AttestationManager;
import org.apache.log4j.Logger;

import com.cloud.event.EventTypes;
import com.cloud.user.Account;

@APICommand(name = "registerAttestationServer", description="Register a new attestation server.",
            responseObject=AttestationServerResponse.class)
public class RegisterAttestationServerCmd extends BaseAsyncCmd {
    public static final Logger s_logger = Logger.getLogger(RegisterAttestationServerCmd.class.getName());

    private static final String s_name = "registerattestationserverresponse";

    @Inject
    private AttestationManager manager;

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name=ApiConstants.URL, type=CommandType.STRING, required=true, description="the host URL")
    private String url;

    @Parameter(name=ApiConstants.USERNAME, type=CommandType.STRING, required=true,
            description="the username with which to register with the attestation server")
    private String username;

    @Parameter(name=ApiConstants.PASSWORD, type=CommandType.STRING, required=true,
            description="the password for the user that registers with the attestation server")
    private String password;

    @Parameter(name=ApiConstants.ZONE_ID, type=CommandType.UUID, entityType=ZoneResponse.class,
            required=true, description="the ID of the Zone")
    private Long zoneid;

    @Parameter(name=ApiConstants.NAME, type=CommandType.UUID, required=false,
            description="name to identify the attestaiton server")
    private String name;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public String getPassword() {
        return password;
    }

    public String getUrl() {
        return url;
    }

    public String getUsername() {
        return username;
    }

    public Long getZoneid() {
        return zoneid;
    }

    public String getName() {
        return name;
    }

    /////////////////////////////////////////////////////
    /////////////// API Implementation///////////////////
    /////////////////////////////////////////////////////

    @Override
    public String getCommandName() {
        return s_name;
    }

    @Override
    public long getEntityOwnerId() {
        return Account.ACCOUNT_ID_SYSTEM;
    }

    @Override
    public String getEventType() {
        return EventTypes.EVENT_ATTESTATION_SERVER_REGISTER;
    }

    @Override
    public String getEventDescription() {
        return  "Registering an attestation server.";
    }

    @Override
    public void execute() {
        try {
            AttestationServerResponse response = manager.registerAttestationServer(this);
            response.setObjectName("attestationserver");
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        } catch (Exception e) {
            s_logger.warn("Exception: ", e);
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }
}
