package com.project.nadaum.accountbook.model.dao;

import java.util.List;
import java.util.Map;

import com.project.nadaum.accountbook.model.vo.AccountBook;

public interface AccountBookDao {

	int insertAccount(AccountBook account);

	List<AccountBook> selectAllAccountList(String id);

	int deleteAccount(String code);

	List<AccountBook> monthlyTotalIncome(String id);

	String monthlyAccount(String id);

	List<AccountBook> income_expense_filter(Map<String, Object> param);	

}
