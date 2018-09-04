package cc.mrbird.common.xss;

import org.apache.commons.lang.BooleanUtils;
import org.apache.commons.lang.StringUtils;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

import javax.servlet.*;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Arrays;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;


public class XssFilter implements Filter {

    public static   Logger logger = LoggerFactory.getLogger(XssFilter.class);

    public static  boolean IS_INCLUDE_RICH_TEXT =false;

    public List<String> excludes =new ArrayList<>();


    @Override
    public void init(FilterConfig filterConfig) throws ServletException {
        logger.info("------ xssfilter is init-------- ");
        String isIncludeRichText = filterConfig.getInitParameter("isIncludeRichText");
        if(StringUtils.isNotBlank(isIncludeRichText)){
            IS_INCLUDE_RICH_TEXT= BooleanUtils.toBoolean(isIncludeRichText);
        }
        String temp = filterConfig.getInitParameter("excludes");
        if (temp!=null){
            String[] url = temp.split(",");
            excludes.addAll(Arrays.asList(url));
        }

    }

    public boolean handleExcludeURL(HttpServletRequest request){
        if (excludes==null || excludes.isEmpty()){
            return  false;
        }
        String servletPath = request.getServletPath();
        return  excludes.stream().map(pattern -> Pattern.compile("^"+pattern))
                .map(p->p.matcher(servletPath)).anyMatch(Matcher::find);
    }

    @Override
    public void doFilter(ServletRequest servletRequest, ServletResponse servletResponse, FilterChain filterChain) throws IOException, ServletException {

        HttpServletRequest request= (HttpServletRequest) servletRequest;
        if (handleExcludeURL(request)){
            filterChain.doFilter(servletRequest,servletResponse);
            return;
        }
        XssHttpServletRequestWrapper xssRequest = new XssHttpServletRequestWrapper((HttpServletRequest) request,
                IS_INCLUDE_RICH_TEXT);
        filterChain.doFilter(xssRequest, servletResponse);
    }

    @Override
    public void destroy() {

    }
}
