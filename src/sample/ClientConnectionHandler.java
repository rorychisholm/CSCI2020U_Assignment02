package sample;

import java.io.*;
import java.net.Socket;

/**
 * Created by 100560820 on 3/27/2017.
 */
// "127.0.0.1 is the loopback Internet protocol (IP) address also referred to as the localhost."
public class ClientConnectionHandler implements Runnable {

    ///////////////////////Taken From HttpRequestHandler///////////////////////
        public static String ROOT = "ServerStorage";
        private Socket socket;
        private DataOutputStream out;
        private PrintWriter out2;

        public ClientConnectionHandler(Socket socket) {
            this.socket = socket;
        }

        public void run() {
            try {

                InputStream is = socket.getInputStream();
                BufferedReader in = new BufferedReader(
                        new InputStreamReader(is)
                );

                OutputStream os = socket.getOutputStream();
                //out = new DataOutputStream(os);
                out2 = new PrintWriter(os);

                String request = in.readLine();
                String[] requestParts = request.split(" ");// CMD Uri

                String command = requestParts[0]; // GET
                if (command.equalsIgnoreCase("DIR")) {
                    cmdDIR();
                }else if (command.equalsIgnoreCase("DOWNLOAD")){
                    cmdDOWNLOAD(requestParts[1]);
                } else {
                    // 405 - method not allowed (invalid HTTP method)
                    System.out.println ("CMD not found.");
                    //sendError(405, "Method Not Allowed");
                }
                socket.close();
            }catch (FileNotFoundException e){
               System.out.println("FileNotFoundException");
            } catch (IOException e) {
                e.printStackTrace();
            }
        }

        private void cmdDIR(){
            System.out.println("cmd DIR Detected");
            String toSend = "";
            File baseDir = new File(ROOT);
            File fileList[] = baseDir.listFiles();
            for (int i = 0; i< fileList.length; i++){
                toSend += fileList[i].getName();
                if (i != (fileList.length - 1)){
                    toSend += " ";
                }
            }
            out2.print(toSend);
            out2.flush();
        }

        private void cmdDOWNLOAD(String fileName) throws IOException{
            System.out.println("cmd DOWNLOAD Detected");
            String toSend = "", line = "";
            File file = new File(ROOT, fileName);
            BufferedReader in = new BufferedReader(new FileReader(file));
            while ((line = in.readLine()) != null){
                toSend += line;
                toSend += "\n";
            }
            out2.print(toSend);
            out2.flush();
        }

        private String getContentType(File file) {
            String filename = file.getName();
            if (filename.endsWith(".html") || filename.endsWith(".htm")) {
                return "text/html";
            } else if (filename.endsWith(".txt")) {
                return "text/plain";
            } else if (filename.endsWith(".css")) {
                return "text/css";
            } else if (filename.endsWith(".js")) {
                return "text/javascript";
            } else if (filename.endsWith(".jpg") || filename.endsWith(".jpeg")) {
                return "image/jpeg";
            } else if (filename.endsWith(".gif")) {
                return "image/gif";
            } else if (filename.endsWith(".png")) {
                return "image/png";
            }
            return "unknown";
        }

        private byte[] readFileContents(File file) throws IOException {
            byte[] content = new byte[(int)file.length()];

            FileInputStream fis = new FileInputStream(file);
            fis.read(content);
            fis.close();
            return content;
        }

        private void sendError(int responseCode, String responseText) throws IOException {
            String errorPage =
                    "<!DOCTYPE html>" +
                            "<html><head><title>" +
                            responseText +
                            "</title></head>" +
                            "<body><h1>" +
                            responseCode + " - " + responseText +
                            "</h1></body></html>";
            sendResponse(responseCode,
                    responseText,
                    "text/html",
                    errorPage.getBytes());
        }

        private void sendResponse(int responseCode, String responseText, String contentType, byte[] content) throws IOException {
            String response = "HTTP/1.1 " +
                    responseCode +
                    " " +
                    responseText +
                    "\r\n";
            out.writeBytes(response);
            out.writeBytes("Content-Type: " + contentType + "\r\n");
            out.writeBytes("Content-Length: " + content.length + "\r\n");
            out.writeBytes("Server: MoonServer v0.9\r\n");
            out.writeBytes("Date: " + (new java.util.Date()).toString() + "\r\n");
            out.writeBytes("Connection: Close\r\n\r\n");
            out.write(content);
            out.flush();
        }
    ///////////////////////Ends Here///////////////////////
}
