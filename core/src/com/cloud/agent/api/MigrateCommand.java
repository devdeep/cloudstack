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
package com.cloud.agent.api;

import com.cloud.agent.api.to.VirtualMachineTO;

public class MigrateCommand extends Command {
    String vmName;
    String destIp;
    String hostGuid;
    String user;
    String password;
    boolean isWindows;
    VirtualMachineTO vmTO;

    protected MigrateCommand() {
    }

    public MigrateCommand(String vmName, String destIp, boolean isWindows, VirtualMachineTO vmTO) {
        this.vmName = vmName;
        this.destIp = destIp;
        this.isWindows = isWindows;
        this.vmTO = vmTO;
        this.user = null;
        this.password = null;
    }

    public boolean isWindows() {
        return isWindows;
    }

    public VirtualMachineTO getVirtualMachine() {
        return vmTO;
    }

    public String getDestinationIp() {
        return destIp;
    }

    public String getVmName() {
        return vmName;
    }

    public void setHostGuid(String guid) {
        this.hostGuid = guid;
    }

    public String getHostGuid() {
        return this.hostGuid;
    }

    public void setUser(String user) {
        this.user = user;
    }

    public String getUser() {
        return this.user;
    }

    public void setPassword(String password) {
        this.password = password;
    }

    public String getPassword() {
        return this.password;
    }

    @Override
    public boolean executeInSequence() {
        return true;
    }
}
