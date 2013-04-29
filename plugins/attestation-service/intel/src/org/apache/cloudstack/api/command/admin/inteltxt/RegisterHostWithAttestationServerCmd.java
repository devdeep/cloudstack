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
import org.apache.cloudstack.api.response.HostResponse;
import org.apache.cloudstack.api.response.RegisterHostWithAttestationServerResponse;
import org.apache.cloudstack.manager.AttestationManager;
import org.apache.log4j.Logger;

import com.cloud.event.EventTypes;
import com.cloud.user.Account;

@APICommand(name="registerHostWithAttestationServer", description="Register host with the attestation server.",
            responseObject=RegisterHostWithAttestationServerResponse.class)
public class RegisterHostWithAttestationServerCmd extends BaseAsyncCmd {
    public static final Logger s_logger = Logger.getLogger(RegisterHostWithAttestationServerCmd.class.getName());

    private static final String s_name = "registerhostwithattestationserverresponse";

    @Inject
    private AttestationManager manager;

    /////////////////////////////////////////////////////
    //////////////// API parameters /////////////////////
    /////////////////////////////////////////////////////

    @Parameter(name = ApiConstants.ID, type = CommandType.UUID, entityType = HostResponse.class,
            required = true, description = "the host to register with the attestation server")
    private Long id;

    @Parameter(name = ApiConstants.PORT, type = CommandType.INTEGER,
            required = false, description = "the port on which the trust agent is running on the host, default is 9999")
    private Integer port;

    /////////////////////////////////////////////////////
    /////////////////// Accessors ///////////////////////
    /////////////////////////////////////////////////////

    public Long getId() {
        return id;
    }

    public Integer getPort() {
        Integer port = 9999;
        if (this.port != null) {
            port = this.port;
        }

        return port;
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
        return EventTypes.EVENT_ATTESTATION_SERVER_HOSTREGISTER;
    }

    @Override
    public String getEventDescription() {
        return  "Registering an attestation server.";
    }

    @Override
    public void execute() {
        try {
            RegisterHostWithAttestationServerResponse response = manager.registerHostWithAttestationServer(this);
            response.setObjectName("registerhostwithattestationserver");
            response.setResponseName(getCommandName());
            this.setResponseObject(response);
        } catch (Exception e) {
            s_logger.warn("Exception: ", e);
            throw new ServerApiException(ApiErrorCode.INTERNAL_ERROR, e.getMessage());
        }
    }
}
