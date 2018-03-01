package com.temolin.application;

import com.temolin.SerialPort;
import com.temolin.SerialPortFinder;

import java.io.File;
import java.io.IOException;
import java.security.InvalidParameterException;

/**
 * Created by linyuan on 2017/1/17.
 */
public class Application extends android.app.Application {

    private SerialPort mSerialPort = null;

    public SerialPort getSerialPort() throws SecurityException, IOException, InvalidParameterException {
			/* Open the serial port */
            //mSerialPort = new SerialPort(new File(path), baudrate, 0);
            mSerialPort = new SerialPort(new File("/dev/ttyMT0"), 9600, 0);
            return mSerialPort;
    }

    public void closeSerialPort() {
        if (mSerialPort != null) {
            mSerialPort.close();
            mSerialPort = null;
        }
    }
}
