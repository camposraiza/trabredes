package servidor;

import servidor.Receptor;
import java.net.Socket;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.sql.Connection;
import java.sql.DriverManager;
import java.util.List;
import java.util.ArrayList;
import java.util.Scanner;
import java.util.logging.Level;
import java.util.logging.Logger;

public class Servidor implements Runnable {
    private ServerSocket server;
    
    private List<Receptor> receptores;
    
    private boolean inicializado;
    private boolean executando;
    
    private Thread thread;
    
   
    
    public Servidor(int port) throws Exception{
        receptores = new ArrayList<Receptor>();
        
        inicializado = false;
        executando = false;
        
        open(port);
    }
    
    private void open(int port) throws Exception{
        server = new ServerSocket(port);
        inicializado = true;
    }
    
    private void close(){
        for(Receptor receptor : receptores){
            try{
                receptor.stop();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        
        try{
            server.close();
        }
        catch(Exception e){
            System.out.println(e);
        }
        server = null;
        
        inicializado = false;
        executando = false;
        
        thread = null;
    }
    
    public void start(){
        if(!inicializado || executando){
            return;
        }
        
        executando = true;
        thread = new Thread(this);
        thread.start();
        
    }
    
    public void stop() throws Exception{
        executando = false;
        
        if(thread != null){
            thread.join();
        }
    }
    
    
        
    @Override
    public void run(){
        while(executando){
            try{
                server.setSoTimeout(2500);
                
                Socket socket = server.accept();
                
                Receptor receptor = new Receptor(socket);
                receptor.start();
                
                receptores.add(receptor);
            }
            
            catch(SocketTimeoutException e){
                
            }
            
            catch(Exception e){
                System.out.println(e);
                break;
            }
        }
        
        close();
    }
    
    public static Connection conexao;

    
    public static void main(String[] args) throws Exception {
        int port = 4444;
        Servidor servidor = new Servidor(port);
        
        try {
            Class.forName("org.apache.derby.jdbc.ClientDriver");

            String driverURL = "jdbc:derby://localhost:1527/2017-3-dcc171";

            conexao = DriverManager.getConnection(driverURL, "usuario", "senha");
        } catch (Exception ex) {
            Logger.getLogger(Servidor.class.getName()).log(Level.SEVERE, null, ex);
        }
        
        
        servidor.start();
        System.out.println("Inicializando servidor de porta " + port);
          
        System.out.println("Aperte ENTER para encerrar o servidor");
        new Scanner(System.in).nextLine();
        
        servidor.stop();
        System.out.println("Servidor encerrado");
    }
}

