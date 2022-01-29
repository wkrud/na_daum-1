package com.project.nadaum.accountbook.controller;

import java.io.IOException;
import java.util.HashMap;
import java.util.List;
import java.util.Map;

import javax.servlet.http.HttpServletResponse;

import org.apache.poi.hssf.usermodel.HSSFWorkbook;
import org.apache.poi.ss.usermodel.Row;
import org.apache.poi.ss.usermodel.Sheet;
import org.apache.poi.ss.usermodel.Workbook;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.annotation.AuthenticationPrincipal;
import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestMethod;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseBody;

import com.project.nadaum.accountbook.model.service.AccountBookService;
import com.project.nadaum.accountbook.model.vo.AccountBook;
import com.project.nadaum.member.model.vo.Member;

import lombok.extern.slf4j.Slf4j;

	
@Slf4j
@Controller
@RequestMapping("/accountbook")
public class AccountBookController {
	
	@Autowired
	private AccountBookService accountBookService;

	@RequestMapping(value="/accountbook.do")
	public void accountbook() {}
	
	@RequestMapping(value="/detailChart.do")
	public void detailChart() {}
	
	
	//전체 리스트 출력
	@ResponseBody
	@RequestMapping(value="/selectAllAccountList.do") 
	 public List<AccountBook> selectAllAccountList (@AuthenticationPrincipal Member member, Model model) { 
		String id = member.getId();
		List<AccountBook> accountList = accountBookService.selectAllAccountList(id);
		model.addAttribute("accountList",accountList);
	 
	 return accountList; 
	 }
	
	 // 가계부 추가
	@RequestMapping(value="/accountInsert.do", method=RequestMethod.POST)
	public String insertAccount(AccountBook account) {
		 int result = accountBookService.insertAccount(account); 
		 
		return "redirect:/accountbook/accountbook.do";
	}
	
	// 가계부 삭제
	@ResponseBody
	@RequestMapping(value="/accountDelete.do", method=RequestMethod.POST)
	public Map<String, Object> deleteAccount(@RequestBody Map <String, Object> code) {		
		Map<String, Object> map = new HashMap<String, Object>();
		map.put("code", code.get("code"));	
		int result = accountBookService.deleteAccount(map);
		return map;
	}
	
	//월간 수입, 지출 금액
	@ResponseBody
	@GetMapping(value="/monthlyTotalIncome.do")
	public List<AccountBook> monthlyTotalIncome(String id, Model model) {
		List<AccountBook> incomeList = accountBookService.monthlyTotalIncome(id);
		
		return incomeList;
	}
	
	//월간 총 합계 금액
	@ResponseBody
	@GetMapping(value="/monthlyAccount.do")
	public String monthlyAccount(String id, Model model) {
		String monthlyAccount = accountBookService.monthlyAccount(id);
		log.info("monthlyAccount={}", monthlyAccount);
		//만약 사용자 입력값이 없어서 monthlyAccount합계가 null값이 넘어온다면 해당 변수에 0 대입
		if (monthlyAccount == null) {
			monthlyAccount = "0";
		}
		model.addAttribute(monthlyAccount);
		
		return monthlyAccount;
	}
	
	// 수입, 지출별 정렬
	 @ResponseBody 
	 @PostMapping(value="/incomeExpenseFilter.do") 
	 public List<AccountBook> incomeExpenseFilter(@RequestBody Map<String, Object> param, Model model) {
		 Map<String, Object> map = new HashMap<>();
		 map.put("id", param.get("id"));
		 map.put("incomeExpense", param.get("incomeExpense"));
		 log.debug("map= {}",map);
		 
		 List<AccountBook> incomeList = accountBookService.incomeExpenseFilter(map); 
		 model.addAttribute(incomeList);
	  
		 return incomeList; 
	  }
	 
	 
	 //검색
	 @ResponseBody
	 @RequestMapping(value="/searchList.do", method=RequestMethod.POST )
	 public List<AccountBook> searchList(@RequestParam Map<String, Object> param, Model model) {
		 Map<String, Object> map = new HashMap<>();
		 map.put("incomeExpense", param.get("\"incomeExpense"));
		 map.put("category", param.get("category"));
		 map.put("detail", param.get("detail"));
		 map.put("id", param.get("id"));
			
		 List<AccountBook> list = accountBookService.searchList(map);
		 model.addAttribute(list); 
		 
		return list;
	 }
	 
		/*
		 * //차트
		 * 
		 * @ResponseBody
		 * 
		 * @PostMapping(value="/incomeChart.do",
		 * produces="application/text; charset=UTF-8") public String chartValue
		 * (@RequestBody Map<String, Object> param, Model model) { Map<String, Object>
		 * map = new HashMap<>(); map.put("id", param.get("id"));
		 * map.put("income_expense", param.get("income_expense")); List<Map<String,
		 * Object>> chartValue = accountBookService.chartValue(map);
		 * 
		 * String result = "";
		 * 
		 * for(int i = 0; i < chartValue.size(); i++) { if(result != "" ) { result +=
		 * ","; } result +=
		 * "['"+chartValue.get(i).get("category").toString()+"', "+chartValue.get(i).get
		 * ("total")+"]"; }
		 * 
		 * log.debug("result={}", result);
		 * 
		 * Gson chartData1 = new Gson(); String chartData2 = chartData1.toJson(result);
		 * 
		 * System.out.println(chartData2);
		 * 
		 * return result; }
		 */

			
		@ResponseBody
		@PostMapping(value="/incomeChart.do", produces="application/json; charset=UTF-8") 
		public List<Map<String, Object>> chartValue (@RequestBody Map<String, Object> param, Model model) {
		Map<String, Object> map = new HashMap<>(); 
		map.put("id", param.get("id"));
		map.put("incomeExpense", param.get("incomeExpense")); 
		List<Map<String, Object>> chartValue = accountBookService.chartValue(map);
		log.debug("chartValue={}", chartValue);
			  
		return chartValue ;
	}
			  
		@GetMapping("/excel")
		public void downloadExcep(HttpServletResponse resp, @AuthenticationPrincipal Member member) throws IOException{
			String id = member.getId();
			List<AccountBook> list = accountBookService.selectAllAccountList(id);
			log.debug("list={}",list);
			
			
			Workbook workbook = new HSSFWorkbook();
			Sheet sheet = workbook.createSheet("나다움-" + member.getName()+"님의 가계부 내역");
			int rowNo = 0;
			
			Row headerRow = sheet.createRow(rowNo++);
			headerRow.createCell(0).setCellValue("날짜");
			headerRow.createCell(1).setCellValue("결제수단");			
			headerRow.createCell(2).setCellValue("대분류");
			headerRow.createCell(3).setCellValue("소분류");
			headerRow.createCell(4).setCellValue("내역");
			headerRow.createCell(5).setCellValue("금액");
			
			
			for(AccountBook accountbook : list) {
				Row row = sheet.createRow(rowNo++);
				row.createCell(0).setCellValue(accountbook.getRegDate());
				row.createCell(1).setCellValue(accountbook.getPayment() == "card" ? "카드" : "현금");
				row.createCell(2).setCellValue(accountbook.getIncomeExpense() == "I" ? "수입" : "지출");
				row.createCell(3).setCellValue(accountbook.getCategory());
				row.createCell(4).setCellValue(accountbook.getDetail());
				row.createCell(5).setCellValue(accountbook.getPrice());
			}
			
			resp.setContentType("ms-vnd/excel");
			resp.setHeader("Content-Disposition", "attachment; filename=나다움-" + member.getName()+"님의 가계부 내역.xls");
			
			workbook.write(resp.getOutputStream());
			workbook.close();
			
		}
		 
		 
		 
		 
		 
}

	


