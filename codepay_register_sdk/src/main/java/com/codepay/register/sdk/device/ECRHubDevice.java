package com.codepay.register.sdk.device;

public class ECRHubDevice {
    //server address
    String ws_address;
    //ip address
    String ip_address;
    //terminal sn
    String terminal_sn;
    //port
    String port;
    //server name
    String name;

    public void setName(String name) {
        this.name = name;
    }

    public String getName() {
        return name;
    }

    public String getIp_address() {
        return ip_address;
    }

    public String getPort() {
        return port;
    }

    public void setIp_address(String ip_address) {
        this.ip_address = ip_address;
    }

    public void setPort(String port) {
        this.port = port;
    }

    public void setTerminal_sn(String terminal_sn) {
        this.terminal_sn = terminal_sn;
    }

    public void setWs_address(String ws_address) {
        this.ws_address = ws_address;
    }

    public String getTerminal_sn() {
        return terminal_sn;
    }

    public String getWs_address() {
        return ws_address;
    }
}
