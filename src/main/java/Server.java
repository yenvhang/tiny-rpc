import java.io.IOException;

/**
 * Created by yeyh on 2018/6/12.
 */
public interface Server {
    /**
     * ����ر�
     */
    void stop();

    /**
     * ��������
     */
    void start() throws IOException;

    /**
     * ע�����
     * @param inf
     * @param impl
     */
    void registry(Class inf,Class impl) throws ClassNotFoundException;
}
