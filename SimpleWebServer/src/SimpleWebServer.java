/***********************************************************************

   SimpleWebServer.java


   This toy web server is used to illustrate security vulnerabilities.
   This web server only supports extremely simple HTTP GET requests.

   This file is also available at http://www.learnsecurity.com/ntk
 
***********************************************************************/
 
//package com.learnsecurity; 

import java.io.*;                                         
import java.net.*;                                        
import java.util.*;

import javax.net.ssl.SSLServerSocketFactory;

import java.nio.charset.StandardCharsets;
import java.io.IOException;
public class SimpleWebServer {                            
 
    /* Run the HTTP server on this TCP port. */           
    private static final int PORT = 8080;                 
 
    /* The socket used to process incoming connections
       from web clients */
    private ServerSocket dServerSocket;            
   
    public SimpleWebServer () throws Exception {          
     	dServerSocket = SSLServerSocketFactory.getDefault().createServerSocket(PORT);          
        }                                                     
 
    public void run() throws Exception {                 
	while (true) {                                   
 	    /* wait for a connection from a client */
 	    Socket s = dServerSocket.accept();           
 
 	    /* then process the client's request */
 	    processRequest(s); 
 	    s.close();
 	}  
	
    }                                                    
 
    /* Reads the HTTP request from the client, and
       responds with the file the user requested or
       a HTTP error code. */
    public void processRequest(Socket s) throws Exception { 
 	/* used to read data from the client */ 
 	
    	
    	BufferedReader br =                                 
 	    new BufferedReader (
				new InputStreamReader (s.getInputStream(),StandardCharsets.UTF_8)); 
 
 	/* used to write data to the client */
 	OutputStreamWriter osw =                            
 	    new OutputStreamWriter (s.getOutputStream(),StandardCharsets.UTF_8);  
     
 	/* read the HTTP request from the client */
 	String request = br.readLine();                    
	br.close();
 	String command = null;                             
 	String pathname = null;                            
     
	 /* parse the HTTP request */
	 if(request!=null) {
 	StringTokenizer st = 
	    new StringTokenizer (request, " ");               
 
 	command = st.nextToken();                       
	 pathname = st.nextToken();  } 
	 
	 else{
		 br.close();
		 osw.close();
		 s.close();
 	    return; 
	 }                    
 
	if ("GET".equals(command)) {                    
	    /* if the request is a GET
	       try to respond with the file
	       the user is requesting */
	    serveFile (osw,pathname);                   
 	}                                              
 	else {                                         
	    /* if the request is a NOT a GET,
	       return an error saying this server
	       does not implement the requested command */
	    osw.write ("HTTP/1.0 501 Not Implemented\n\n");
 	}                                               
 	
 	/* close the connection to the client */
	 osw.close();
	 br.close();
	 s.close();                                  
    }                                                   
 
    public void serveFile (OutputStreamWriter osw,      
			   String pathname) throws Exception {
 	FileReader fr=null;                                 
 	int c=-1;                                           
 	StringBuffer sb = new StringBuffer();
       
 	/* remove the initial slash at the beginning
 	   of the pathname in the request */
 	if (pathname.charAt(0)=='/')  {                      
 	    pathname=pathname.substring(1);  }               
 	
 	/* if there was no filename specified by the
 	   client, serve the "index.html" file */
 	if ("".equals(pathname)) {                           
 	    pathname="index.html"; }                         
 
 	/* try to open file specified by pathname */
 	try {                                               
 	    fr = new FileReader (pathname,StandardCharsets.UTF_8);   
 	    c = fr.read();                                  
 	}   catch (RuntimeException e) {

		throw e;
	} 								
 	catch (Exception e) {                               
 	    /* if the file is not found,return the
 	       appropriate HTTP response code  */
		 osw.write ("HTTP/1.0 404 Not Found\n\n");
		 
 	    return;                                         
 	} 
 	finally {
 		if(fr!=null) { fr.close();}
 	}
 
 	/* if the requested file can be successfully opened
 	   and read, then return an OK response code and
 	   send the contents of the file */
 	osw.write ("HTTP/1.0 200 OK\n\n");                    
 	while (c != -1) {       
	    sb.append((char)c);                            
 	    c = fr.read();   
 	 	System.out.println("It works../nc = "+c);

 	}                                                   
	 osw.write (sb.toString());
	 fr.close();                                  
    }                                                       
 
    /* This method is called when the program is run from
       the command line. */
    public static void main (String argv[]) throws Exception { 
 
 	/* Create a SimpleWebServer object, and run it */
 	SimpleWebServer sws = new SimpleWebServer();           
 	sws.run();  
    }                                                          
}                                                              
