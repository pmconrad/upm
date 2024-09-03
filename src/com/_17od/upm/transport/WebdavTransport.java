/*
 * (C) 2013 Peter Conrad <conrad@quisquis.de>
 *
 * This file is part of Universal Password Manager.
 *
 * Universal Password Manager is free software; you can redistribute it and/or modify
 * it under the terms of the GNU General Public License as published by
 * the Free Software Foundation; either version 2 of the License, or
 * (at your option) any later version.
 *
 * Universal Password Manager is distributed in the hope that it will be useful,
 * but WITHOUT ANY WARRANTY; without even the implied warranty of
 * MERCHANTABILITY or FITNESS FOR A PARTICULAR PURPOSE.  See the
 * GNU General Public License for more details.
 *
 * You should have received a copy of the GNU General Public License
 * along with Universal Password Manager; if not, write to the Free Software
 * Foundation, Inc., 51 Franklin St, Fifth Floor, Boston, MA  02110-1301  USA
 */
package com._17od.upm.transport;

import java.io.ByteArrayOutputStream;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.IOException;
import java.io.InputStream;

import java.net.MalformedURLException;

import org.apache.commons.httpclient.HostConfiguration;
import org.apache.commons.httpclient.HttpClient;
import org.apache.commons.httpclient.HttpURL;
import org.apache.commons.httpclient.HttpException;
import org.apache.commons.httpclient.HttpsURL;
import org.apache.commons.httpclient.UsernamePasswordCredentials;

import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

import org.apache.webdav.lib.WebdavResource;

/**
 *
 * @author Peter Conrad
 */
public class WebdavTransport extends HTTPTransport {
    private static class MyDavResource extends WebdavResource {
        private HostConfiguration hostConfig;

        private MyDavResource(HttpClient httpClient) {
            super(httpClient);
            hostConfig = httpClient.getHostConfiguration();
        }

        public void setHttpURL(String url) throws HttpException, IOException {
            HttpURL httpUrl;
            if (url.startsWith("webdavs")) {
                httpUrl = new HttpsURL("http" + url.substring(6));
            } else if (url.startsWith("webdav")) {
                httpUrl = new HttpURL("http" + url.substring(6));
            } else if (url.startsWith("https")) {
                httpUrl = new HttpsURL(url);
            } else {
                httpUrl = new HttpURL(url);
            }
            if (hostCredentials != null) {
                UsernamePasswordCredentials cred = (UsernamePasswordCredentials) hostCredentials;
                httpUrl.setUserinfo(cred.getUserName(), cred.getPassword());
            }
            if (hostConfig != null) { // avoid "host parameter is null"
                hostConfig.setHost(httpUrl.getHost());
            }
            setHttpURL(httpUrl);
        }
    }

    private static Log log = LogFactory.getLog(WebdavTransport.class);

    private WebdavResource open() throws TransportException {
        return new MyDavResource(client);
    }

    public void put(String targetLocation, File file,
                    String username, String password) throws TransportException {
        log.info("Webdav.put('" + targetLocation + "', '" + file.getPath()
                 + "', " + (username != null ? "'" + username + "'" : "null")
                 + ", ...)");
        WebdavResource davRes = open();
        try {
            if (username != null) {
                davRes.setCredentials(new UsernamePasswordCredentials(username, password));
            }
            davRes.setHttpURL(addTrailingSlash(targetLocation) + file.getName());
            if (!davRes.putMethod(file)) {
                throw new TransportException("There's been some kind of problem uploading a file to the WebDAV server.");
            }
        } catch (FileNotFoundException e) {
            throw new TransportException(e);
        } catch (MalformedURLException e) {
            throw new TransportException(e);
        } catch (HttpException e) {
            throw new TransportException(e);
        } catch (IOException e) {
            throw new TransportException(e);
        } finally {
            try {
                davRes.close();
            } catch (IOException ignore) {}
        }
    }

    public byte[] get(String url, String username, String password)
            throws TransportException {
        log.info("Webdav.get('" + url + "', "
                 + (username != null ? "'" + username + "'" : "null")
                 + ", ...)");
        ByteArrayOutputStream buffer = new ByteArrayOutputStream();
        WebdavResource davRes = open();
        try {
            if (username != null) {
                davRes.setCredentials(new UsernamePasswordCredentials(username, password));
            }
            davRes.setHttpURL(url);
            if (davRes.isCollection()) {
                throw new TransportException("URL is a collection!");
            }
            byte buf[] = new byte[1024];
            int c;
            InputStream is = davRes.getMethodData();
            try {
                do {
                    c = is.read(buf);
                    if (c > 0) {
                        buffer.write(buf, 0, c);
                    }
                } while (c >= 0);
            } finally {
                is.close();
            }
        } catch (FileNotFoundException e) {
            throw new TransportException(e);
        } catch (MalformedURLException e) {
            throw new TransportException(e);
        } catch (HttpException e) {
            throw new TransportException(e);
        } catch (IOException e) {
            throw new TransportException(e);
        } finally {
            try {
                davRes.close();
            } catch (IOException ignore) {}
        }
        return buffer.toByteArray();
    }

    public void delete(String targetLocation, String name,
                       String username, String password) throws TransportException {
        /* delete is only ever called right before put, so it isn't useful. Skip it.
        log.info("Webdav.delete('" + targetLocation + "', '" + name
                 + "', " + (username != null ? "'" + username + "'" : "null")
                 + ", ...)");
        WebdavResource davRes = open();
        try {
            if (username != null) {
                davRes.setCredentials(new UsernamePasswordCredentials(username, password));
            }
            davRes.setHttpURL(addTrailingSlash(targetLocation) + name);
            davRes.deleteMethod();
        } catch (FileNotFoundException e) {
            throw new TransportException(e);
        } catch (MalformedURLException e) {
            throw new TransportException(e);
        } catch (HttpException e) {
            throw new TransportException(e);
        } catch (IOException e) {
            throw new TransportException(e);
        } finally {
            try {
                davRes.close();
            } catch (IOException ignore) {}
        }
        */
    }
}
