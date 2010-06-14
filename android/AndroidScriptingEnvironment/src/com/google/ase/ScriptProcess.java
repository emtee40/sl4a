/*
 * Copyright (C) 2010 Google Inc.
 *
 * Licensed under the Apache License, Version 2.0 (the "License"); you may not
 * use this file except in compliance with the License. You may obtain a copy of
 * the License at
 *
 * http://www.apache.org/licenses/LICENSE-2.0
 *
 * Unless required by applicable law or agreed to in writing, software
 * distributed under the License is distributed on an "AS IS" BASIS, WITHOUT
 * WARRANTIES OR CONDITIONS OF ANY KIND, either express or implied. See the
 * License for the specific language governing permissions and limitations under
 * the License.
 */

package com.google.ase;

import java.net.InetSocketAddress;

public class ScriptProcess {

  private static enum State {

    ALIVE("Alive"), DEAD("Dead");

    private final String name;

    State(String name) {
      this.name = name;
    }

    @Override
    public String toString() {
      return name;
    }
  };

  private final int mServerPort;
  private final ScriptLauncher mLauncher;
  private final AndroidProxy mProxy;
  private final long mStartTime;
  private volatile State myState;

  public ScriptProcess(AndroidProxy proxy, ScriptLauncher launcher) {
    myState = State.ALIVE;
    mStartTime = System.currentTimeMillis();
    mServerPort = proxy.getAddress().getPort();
    mLauncher = launcher;
    mProxy = proxy;
  }

  public int getPort() {
    return mServerPort;
  }
  
  public int getPID() {
    return mLauncher.getPid();
  }

  public long getStartTime() {
    return mStartTime;
  }

  // getUpTime

  public String getScriptName() {
    if (mLauncher == null) {
      return null;
    }
    return mLauncher.getScriptName();
  }

  public String getServerName() {
    if (mProxy == null) {
      return null;
    }
    InetSocketAddress address = mProxy.getAddress();
    return address.getHostName();
  }

  public String getState() {
    return myState.toString();
  }

  public void kill() {
    myState = State.DEAD;
    if (mLauncher != null) {
      mLauncher.kill();
    }
    if (mProxy != null) {
      mProxy.shutdown();
    }
  }

  @Override
  public String toString() {
    StringBuffer info = new StringBuffer();
    InetSocketAddress address = mProxy.getAddress();

    info.append(String.format("Running network service on: %s:%d", address.getHostName(), address
        .getPort()));
    if (mLauncher != null) {
      String scriptName = mLauncher.getScriptName();
      info.append("\nRunning script service: ");
      info.append(scriptName);
    }
    return info.toString();
  }

  public String getUptime() {
    long ms = System.currentTimeMillis() - mStartTime;
    StringBuffer buffer = new StringBuffer();
    int days = (int) (ms / (1000 * 60 * 60 * 24));
    int hours = (int) (ms % (1000 * 60 * 60 * 24)) / 3600000;
    int minutes = (int) (ms % 3600000) / 60000;
    int seconds = (int) (ms % 60000) / 1000;
    if (days != 0) {
      buffer.append(String.format("%02d:", days));
    }
    if (hours != 0) {
      buffer.append(String.format("%02d:", hours));
    }
    buffer.append(String.format("%02d:%02d", minutes, seconds));
    return buffer.toString();
  }

}
