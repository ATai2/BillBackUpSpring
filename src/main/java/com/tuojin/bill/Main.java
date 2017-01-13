package com.tuojin.bill;

import com.tuojin.bill.utils.Context;
import com.tuojin.bill.utils.DBHelper;

/**
 * Created by Administrator on 2017/1/12.
 */
public class Main {
    private IOperator operator=new Operator();
    public static void main(String[] args) {
//        Main main=new Main();

        DBHelper dbHelper=new DBHelper();
        Context context=new Context();
//        main.operator.checkTable();

    }
}
