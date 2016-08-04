package com.cybermkd.filter;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;
import javax.servlet.http.HttpServletResponseWrapper;
import java.io.*;
import java.util.zip.GZIPOutputStream;

public class GzipFilter implements Filter {
    private String encoding;


    public void init(FilterConfig filterConfig) throws ServletException {
        encoding = "UTF-8";
    }


    public void doFilter(ServletRequest request, ServletResponse response,
                         FilterChain chain) throws IOException, ServletException {

        HttpServletRequest requ = (HttpServletRequest) request;
        HttpServletResponse resp = (HttpServletResponse) response;
        MyHttpServletResponseWrapper myresponse = new MyHttpServletResponseWrapper(resp);

        chain.doFilter(requ, myresponse);

        //对数据进行压缩  
        ByteArrayOutputStream baos = new ByteArrayOutputStream();
        //这里需要的是一个底层流  
        GZIPOutputStream gzipout = new GZIPOutputStream(baos);

        //获取原始数据  
        byte[] b = myresponse.getOldBytes();

        //对数据进行压缩,并存在baos这个底层流中  
        gzipout.write(b);
        gzipout.close();  //确保数据被刷到底层流baos中  
        b = baos.toByteArray();

        //告诉浏览器数据是经过gzip压缩的  
        resp.setHeader("content-encoding", "gzip");
        //告诉浏览器数据的大小  
        resp.setContentLength(b.length);
        //将数据返回给浏览器  
        resp.getOutputStream().write(b);
    }

    private class MyHttpServletResponseWrapper extends HttpServletResponseWrapper {
        private HttpServletResponse response;
        private ByteArrayOutputStream baos = new ByteArrayOutputStream();
        private PrintWriter pw;

        public MyHttpServletResponseWrapper(HttpServletResponse response) {
            super(response);
            this.response = response;
        }

        public byte[] getOldBytes() {
            if(pw != null){
                //确保pw里的缓冲区的数据刷到baos中  
                pw.close();
            }
            return baos.toByteArray();
        }

        @Override
        public ServletOutputStream getOutputStream() throws IOException {
            //自定义一个输出流并将底层流baos传入, 这样,原始数据就能被写入底层流baos中,以便让myresponse对象获取  
            return new ZipStream(baos);
        }

        @Override
        public PrintWriter getWriter() throws IOException {
            //1. 为了避免调用者在执行response.getWriter().print("xxxxxx")方法时出现"错误500: getWriter() has already been called for this response"  
            //   因为在一次HTTP请求中, getWriter()和getOutputStream()两个方法不能同时被执行 [ 上面第60行已经执行了getOutputStream()方法 ]   
            //   因此在这里重写getWriter()方法, 且内部使用OutputStream来处理  
            //2. 因为PrintWriter是一个 字符流, 字符流本来是有编码的, 字符流转换为 字节流的时候 很多时候丢失编码的 .  
            //   所以这里通过使用转换流来解决乱码问题 ..  
            pw = new PrintWriter(new OutputStreamWriter(baos, encoding));
            return pw;
        }
    }

    private class ZipStream extends ServletOutputStream {
        private OutputStream baos;

        public ZipStream(OutputStream baos) {
            this.baos = baos;
        }

        @Override
        public void write(int b) throws IOException {
            baos.write(b);
        }


        @Override
        public boolean isReady() {
            return true;
        }

        @Override
        public void setWriteListener(WriteListener writeListener) {

        }
    }


    public void destroy() {
        // TODO Auto-generated method stub  
    }
}  