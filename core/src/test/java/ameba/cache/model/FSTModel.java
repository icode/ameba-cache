package ameba.cache.model;

import javax.persistence.Entity;
import javax.persistence.Transient;
import java.util.List;

/**
 * @author icode
 */
@Entity
public class FSTModel extends BaseModel {
    private String f1;
    private Integer f2;
    @Transient
    private List f3;

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getF1() {
        return f1;
    }

    public void setF1(String f1) {
        this.f1 = f1;
    }

    public Integer getF2() {
        return f2;
    }

    public void setF2(Integer f2) {
        this.f2 = f2;
    }

    public List getF3() {
        return f3;
    }

    public void setF3(List f3) {
        this.f3 = f3;
    }
}
