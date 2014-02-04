/*
 * Licensed to the Apache Software Foundation (ASF) under one
 * or more contributor license agreements.  See the NOTICE file
 * distributed with this work for additional information
 * regarding copyright ownership.  The ASF licenses this file
 * to you under the Apache License, Version 2.0 (the
 * "License"); you may not use this file except in compliance
 * with the License.  You may obtain a copy of the License at
 *
 *   http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing,
 * software distributed under the License is distributed on an
 * "AS IS" BASIS, WITHOUT WARRANTIES OR CONDITIONS OF ANY
 * KIND, either express or implied.  See the License for the
 * specific language governing permissions and limitations
 * under the License.
 */
package org.apache.cloudstack.storage.command;

import java.util.Map;

import com.cloud.agent.api.Answer;
import com.cloud.agent.api.to.DiskTO;

public class AttachAnswer extends Answer {
    private DiskTO disk;
    private Map<String, String> diskDetails;

    public AttachAnswer() {
        super(null);
    }

    public AttachAnswer(DiskTO disk) {
        super(null);
        this.setDisk(disk);
    }

    public AttachAnswer(DiskTO disk, Map<String, String> diskDetails) {
        super(null);
        this.setDisk(disk);
        this.setDiskDetails(diskDetails);
    }

    public AttachAnswer(String errMsg) {
        super(null, false, errMsg);
    }

    public DiskTO getDisk() {
        return disk;
    }

    public void setDisk(DiskTO disk) {
        this.disk = disk;
    }

    public Map<String, String> getDiskDetails() {
        return diskDetails;
    }

    public void setDiskDetails(Map<String, String> diskDetails) {
        this.diskDetails = diskDetails;
    }
}
