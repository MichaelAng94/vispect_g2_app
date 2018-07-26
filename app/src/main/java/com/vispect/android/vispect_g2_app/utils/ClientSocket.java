package com.vispect.android.vispect_g2_app.utils;


import java.io.BufferedInputStream;
import java.io.BufferedOutputStream;
import java.io.DataInputStream;
import java.io.DataOutputStream;
import java.io.IOException;
import java.io.OutputStream;
import java.net.InetSocketAddress;
import java.net.Socket;

public class ClientSocket {
    private String ip;

    private int port;

    private static Socket socket = null;

    static DataOutputStream out = null;

    DataInputStream getMessageStream = null;
    DataOutputStream getoutpustream = null;

    public ClientSocket(String ip, int port) {
        this.ip = ip;
        this.port = port;
    }

    /**
     * 创建socket连接
     * @throws Exception exception
     */
    public void CreateConnection() throws Exception {
        XuLog.d("DrivingVideosActivity","开始在连接socket了");
        try {
            socket = new Socket();
            socket.connect(new InetSocketAddress(ip, port), 500);
        } catch (Exception e) {
            e.printStackTrace();
            XuLog.d("DrivingVideosActivity", "new Socket出错了");
            if (socket != null)
                socket.close();
            throw e;
        } finally {
        }
    }

    public static void sendMessage(String msg) throws Exception {
        try {
            out = new DataOutputStream(socket.getOutputStream());
            if (socket != null && socket.isConnected()) {
                if (!socket.isOutputShutdown()) {
                    byte[] array = new byte[msg.length() / 2];
                    for (int i = 0; i < msg.length() / 2; i++) {
                        array[i] = Integer.valueOf(msg.substring(i * 2, i * 2 + 2), 16).byteValue();
                    }
                    XuLog.i("send commend 9000:" + msg);
                    try {
                        out.write(array);
                    } catch (IOException e) {
                        e.printStackTrace();
                    }
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            if (out != null)
                out.close();
            throw e;
        } finally {
        }
    }

    public DataInputStream getMessageStream() throws Exception {
        try {
            getMessageStream = new DataInputStream(new BufferedInputStream(socket.getInputStream()));
            return getMessageStream;
        } catch (Exception e) {
            e.printStackTrace();
            if (getMessageStream != null)
                getMessageStream.close();
            throw e;
        } finally {
        }
    }

    public DataOutputStream getOutpuStream() throws Exception {
        try {
            getoutpustream = new DataOutputStream(new BufferedOutputStream(socket.getOutputStream()));
            return getoutpustream;
        } catch (Exception e) {
            e.printStackTrace();
            if (getoutpustream != null)
                getoutpustream.close();
            throw e;
        } finally {
        }
    }


    public OutputStream getOutpu() throws Exception {
        try {
            return socket.getOutputStream();
        } catch (Exception e) {
            e.printStackTrace();
            if (getoutpustream != null)
                getoutpustream.close();
            throw e;
        } finally {
        }
    }

    public void shutDownConnection() {
        try {
            if (out != null)
                out.close();
            if (getMessageStream != null)
                getMessageStream.close();
            if (socket != null)
                socket.close();
        } catch (Exception e) {

        }
    }

    public boolean isConnection() {
        if (socket != null) {
            return socket.isConnected();
        }
        return false;
    }
}