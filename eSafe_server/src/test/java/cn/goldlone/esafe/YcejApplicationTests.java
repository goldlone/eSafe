package cn.goldlone.esafe;

import cn.goldlone.esafe.mapper.GeoMapper;
import cn.goldlone.esafe.monitor.ChealPoint;
import cn.goldlone.esafe.po.GeoMsg;
import cn.goldlone.esafe.service.GeoService;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.junit4.SpringRunner;

import java.text.SimpleDateFormat;

@RunWith(SpringRunner.class)
@SpringBootTest
public class YcejApplicationTests {

	@Test
	public void contextLoads() {
	}

	@Autowired
	private GeoMapper am;

	@Test
	public void isExist() {
//		System.out.println(am.selectTable("gold1"));
//		System.out.println(am.selectTable("user1"));
	}

//	@Test
	public void counts() {
//		System.out.println(am.getCounts(new SelectCount("gold1", "latitude", 0,
//				"after", Date.valueOf("1900-01-01"), new Date(System.currentTimeMillis()))));
	}

//	@Test
	public void saveTail() {
		if(am.selectUserTail("goldl")==0) {
			am.insertTail(new GeoMsg("gold1", 1, "", "", "", ""));
			am.insertTail(new GeoMsg("gold1", 0, "", "", "", ""));
			System.out.println("插入");
		} else {
			am.updateTail(new GeoMsg("gold1", 0, "", "", "", ""));
			am.updateTail(new GeoMsg("gold1", 0, "", "", "", ""));
			System.out.println("更新");
		}
	}

	@Autowired
	private ChealPoint cp;

	@Test
	public void train() {
		// 训练
		cp.train("gold1");

		// 异常检测
		if(cp.detect("gold1", 0, 37.7952201984, 112.5897145271,"morn"))
			System.out.println("***** 轨迹异常 *****");
		else
			System.out.println("***** 轨迹正常 *****");
	}

	public static void main(String[] args) {
		java.util.Date date = new java.util.Date();
		SimpleDateFormat df = new SimpleDateFormat("HH");
		int hour = Integer.parseInt(df.format(date));
		System.out.println(hour);
	}

	@Autowired
	private GeoService gs;

	@Test
	public void getCluster() {
		gs.getClusterCenter("gold1");
	}
}
