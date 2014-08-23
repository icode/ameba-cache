package ameba.cache;

import javax.ws.rs.NameBinding;
import java.lang.annotation.Retention;
import java.lang.annotation.RetentionPolicy;

/**
 * @author icode
 */
@Retention(RetentionPolicy.RUNTIME)
@NameBinding
public @interface Cached {
    /**
     * 过期时间字符串，如 1d , 1h , 3mn , 6s
     * 优先使用此属性作为过期时间，如未配置则使用 {@link #expiration()}
     *
     * @return 过期时间字符串 默认为 空字符串（""）
     */
    String value() default "";

    /**
     * 缓存的过期时间，秒未单位
     * 如配置了 {@link #value()} 则忽略此项
     *
     * @return 过期时间秒数 默认为 0
     */
    int expiration() default 0;

    /**
     * 是否同步存储缓存
     *
     * @return 是否同步存储缓存 默认未 false
     */
    boolean sync() default false;

    /**
     * 自定义缓存键，如果位设置则用当前访问地址作为键
     *
     * @return 缓存键 默认为 空字符串（""）
     */
    String key() default "";

    /**
     * 每次访问缓存时是否更新缓存
     *
     * @return 是否更新过期时间 默认为 false
     */
    boolean touch() default false;

    /**
     * url的query字符串是否作为键的一部分
     *
     * @return url的query字符串是否作为键的一部分 默认为 true
     */
    boolean keyWithQuery() default true;
}