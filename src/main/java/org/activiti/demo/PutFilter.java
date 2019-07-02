package org.activiti.demo;

import org.springframework.core.annotation.Order;
import org.springframework.stereotype.Component;
import org.springframework.web.filter.HttpPutFormContentFilter;

import javax.servlet.annotation.WebFilter;

/**
 * springboot中自带HttpPutFormContentFilter用于处理put请求，如果获取不到参数，可以将该过滤器优先级提高
 */
@Component
@WebFilter(urlPatterns = "/*", filterName = "putFilter")
@Order(Integer.MIN_VALUE)
public class PutFilter extends HttpPutFormContentFilter {

}

