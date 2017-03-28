package sample;

import java.io.*;
import java.net.*;
import java.net.Socket;

/**
 * Created by 100560820 on 3/27/2017.
 */
// "127.0.0.1 is the loopback Internet protocol (IP) address also referred to as the localhost."
public class ClientConnectionHandler implements Runnable {

    ///////////////////////Taken From HttpRequestHandler///////////////////////
        public static String WEB_ROOT = "wwwRoot";

        private Socket socket;
        private DataOutputStream out;

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
                out = new DataOutputStream(os);
                //GET /index.html HTTP/1.1
                String request = in.readLine();
                String[] requestParts = request.split(" ");

                if (requestParts.length < 3) {
                    sendError(400, "Bad request"); // syntax error in HTTP request
                    return;
                }

                String command = requestParts[0];
                String uri = requestParts[1];

                if (command.equalsIgnoreCase("GET")) {
                    File localFile = new File(WEB_ROOT, uri);
                    if (localFile.exists()) {
                        // send the file's contents to the socket
                        byte[] contents = readFileContents(localFile);
                        sendResponse(200,
                                "Ok",
                                getContentType(localFile),
                                contents);
                    } else {
                        // 404 - file not found
                        sendError(404, "Not found");
                    }
                } else {
                    // 405 - method not allowed (invalid HTTP method)
                    sendError(405, "Method Not Allowed");
                }

                socket.close();
            } catch (IOException e) {
                e.printStackTrace();
            }
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
