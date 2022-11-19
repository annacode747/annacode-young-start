package cn.annacode.youngStart.org.framework.enumerate;

import lombok.AllArgsConstructor;
import lombok.Getter;

@AllArgsConstructor
@Getter
public enum ScopeType {

    PROTOTYPE("prototype"),
    SINGLETON("singleton");
    private final String type;
}
