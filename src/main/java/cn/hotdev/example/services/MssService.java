package cn.hotdev.example.services;

import cn.hotdev.example.models.mss.MssCreate;
import cn.hotdev.example.models.mss.MssResponse;
import org.springframework.stereotype.Service;

/**
 * Created by andy on 6/10/15.
 */
@Service
public interface MssService {

    public MssResponse support(double lng, double lat) throws Exception;

    public MssResponse create(MssCreate mssCreate) throws Exception;
}
