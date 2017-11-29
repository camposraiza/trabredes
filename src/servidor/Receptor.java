package servidor;

import java.net.Socket;
import java.net.SocketTimeoutException;
import java.io.BufferedReader;
import java.io.InputStreamReader;
import java.io.PrintStream;
import java.sql.Connection;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.Random;
import servidor.Servidor;

public class Receptor implements Runnable{
    private Socket socket;
    
    private BufferedReader in;
    private PrintStream out;
    
    private boolean inicializado;
    private boolean executando;
    
    private Thread thread;
    
    public Receptor(Socket socket) throws Exception{
        this.socket = socket;
        
        this.inicializado = false;
        this.executando = false;
        
        open();
    }
    
    private void open() throws Exception{
        try{
            in = new BufferedReader(new InputStreamReader(socket.getInputStream()));
            out = new PrintStream(socket.getOutputStream());
            inicializado = true;
        }
        catch(Exception e){
            close();
            throw e;
        }
    }
    
    private void close() throws Exception{
        if(in != null){
            try{
                in.close();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        
        if(out != null){
            try{
                out.close();
            }
            catch(Exception e){
                System.out.println(e);
            }
        }
        
        try{
                socket.close();
        }
        catch(Exception e){
                System.out.println(e);
        }
        
        in = null;
        out = null;
        socket = null;
        
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
                socket.setSoTimeout(2500);
                
                String msg ;
                String text = in.readLine();
                
                switch(Integer.parseInt(text)){
                    case 1:
                       msg = in.readLine();
                       msg = Boolean.toString(verificaCliente(Servidor.conexao,msg));
                       out.println(msg);
                       msg = in.readLine();
                       msg = Boolean.toString(verificaSenha(Servidor.conexao,msg));   
                       out.println(msg);
                            
                        
                        
                        break;
                        
                    
                }
                
               
//                System.out.println("Mensagem recebida de cliente [ " +
//                    socket.getInetAddress().getHostName() + " ] " +
//                    socket.getPort() + " " + msg);
//            
                if ("FIM".equals(text)){
                    break;
                }
                out.println(text);
            }
            catch(SocketTimeoutException e){
                
            }
            
            catch(Exception e){
                System.out.println(e.getMessage());
                break;
            }
        }        
    }
    
       private static boolean verificaCliente(Connection conexao, String msg) throws SQLException {
            Statement operacao = conexao.createStatement();
            ResultSet resultado = operacao.executeQuery("SELECT nome FROM cliente WHERE nome ='"+ msg +"'");
           
            return true;
            
       }

    private boolean verificaSenha(Connection conexao, String msg) throws SQLException {
            Statement operacao = conexao.createStatement();
            ResultSet resultado = operacao.executeQuery("SELECT senha FROM cliente WHERE senha =" + msg);
            if(!resultado.next()){
                return false;
            }
            return true;    }
            
}

