package springMVC.HandlerTools;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import java.lang.reflect.Method;

//获得能够执行对应request的handler，是一个执行链，核心就
// 是能够执行的类以及他的前项和后项过滤器
@Data
@NoArgsConstructor
@AllArgsConstructor
public class HandlerExecutionChain {
    Class controllerClass;
    Method controllerMethod;
    Object executionHandler;
}
