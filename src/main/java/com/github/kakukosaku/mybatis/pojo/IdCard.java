package com.github.kakukosaku.mybatis.pojo;

/**
 * Description
 *
 * @author kaku
 * Date    2020/5/31
 */
public class IdCard {
    private int id;
    private String cno;

    public int getId() {
        return id;
    }

    public void setId(int id) {
        this.id = id;
    }

    public String getCno() {
        return cno;
    }

    public void setCno(String cno) {
        this.cno = cno;
    }

    @Override
    public String toString() {
        final StringBuffer sb = new StringBuffer("IdCard{");
        sb.append("id=").append(id);
        sb.append(", cno='").append(cno).append('\'');
        sb.append('}');
        return sb.toString();
    }

}
