package com.ujigu.exam.test;

import javax.annotation.Resource;

import com.xyz.eznuoche.service.OfflineUserCarService;

public class ExportExcel extends BaseJunit4Test{

	@Resource
	private OfflineUserCarService offlineUserCarService;
	
	/*@org.junit.Test
	public void testExport() {
		List<OfflineUserCar> dbDatas = offlineUserCarService.loadCars(0);
		
		List<List<String>> rows = new ArrayList<>();
		for(OfflineUserCar dbData : dbDatas) {
			List<String> row = new ArrayList<>();
			row.add(dbData.getDetailAddr());
			row.add(dbData.getCarBrand());
			row.add(dbData.getDecyptPhone());
			
			rows.add(row);
		}
		
		HSSFWorkbook book = ExportUtil.genExcel(Arrays.asList("所在位置", "车辆品牌", "手机号"), rows, "车主信息");
		ExportUtil.writeExcel(book, "C:/data/car.xls");
	}*/
	
}
