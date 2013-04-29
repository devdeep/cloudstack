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
package org.apache.cloudstack.manager;

import org.apache.cloudstack.api.command.admin.inteltxt.ListAttestationServerCmd;
import org.apache.cloudstack.api.command.admin.inteltxt.RegisterAttestationServerCmd;
import org.apache.cloudstack.api.command.admin.inteltxt.RegisterHostWithAttestationServerCmd;
import org.apache.cloudstack.api.command.admin.inteltxt.UnregisterAttestationServerCmd;
import org.apache.cloudstack.api.response.AttestationServerResponse;
import org.apache.cloudstack.api.response.ListResponse;
import org.apache.cloudstack.api.response.RegisterHostWithAttestationServerResponse;
import org.apache.cloudstack.api.response.SuccessResponse;

import com.cloud.utils.component.Manager;
import com.cloud.utils.component.PluggableService;

public interface AttestationManager extends Manager, PluggableService {
    AttestationServerResponse registerAttestationServer(RegisterAttestationServerCmd cmd);
    SuccessResponse unregisterAttestationServer(UnregisterAttestationServerCmd cmd);
    RegisterHostWithAttestationServerResponse registerHostWithAttestationServer(RegisterHostWithAttestationServerCmd cmd);
    ListResponse<AttestationServerResponse> listAttestationServer(ListAttestationServerCmd cmd);
}
