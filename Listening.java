import com.jcraft.jsch.ChannelSftp;
import com.jcraft.jsch.JSch;
import com.jcraft.jsch.Session;
import java.io.*;

/**
 *
 * @author World
 */
public class Listening
    {
    public static void main(String args[])
    {
    String user = "root";
    String password = "s3nsci0";
    String host = "10.0.55.90";
    int port=22;

    String remoteFile="10.0.55.90/var/ww/Library/Internal%20Document%20Repository/";

    try
        {
        JSch jsch = new JSch();
        Session session = jsch.getSession(user, host, port);
            session.setPassword(password);
            session.setConfig("StrictHostKeyChecking", "no");
        System.out.println("Establishing Connection...");
        session.connect();
            System.out.println("Connection established.");
        System.out.println("Crating SFTP Channel.");
        ChannelSftp sftpChannel = (ChannelSftp) session.openChannel("sftp");
        sftpChannel.connect();
        System.out.println("SFTP Channel created.");
        }
    catch(Exception e){System.err.print(e);}
    }
    }