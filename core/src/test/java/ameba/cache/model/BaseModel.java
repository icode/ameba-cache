package ameba.cache.model;

import ameba.db.model.Model;
import io.ebean.annotation.CreatedTimestamp;
import io.ebean.annotation.UpdatedTimestamp;

import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.MappedSuperclass;
import java.sql.Timestamp;

/**
 * @author icode
 */
@MappedSuperclass
public abstract class BaseModel extends Model {
    @Id
    @GeneratedValue
    public Long id;
    @CreatedTimestamp
    public Timestamp createTime;
    @UpdatedTimestamp
    public Timestamp updateTime;
    /**
     * empid
     */
    public Integer createEmpid;

    public Integer updateEmpid;

}