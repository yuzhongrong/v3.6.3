package model;

import java.io.Serializable;

@SuppressWarnings("serial")
public class RegularRecord implements Serializable {

    private String c_time;
    private String context;
    private String money;
    private int sy_time;
    private String tips;
    private String order_id;
    private String invest_earnings;
    private String invest_days;
    private String invest_end_date;
    private String invest_money;
    private String today_rate;
    private String progress;
    private String status;
    private String asserts_id;
    private String prospective_yield_amt;
    private String url;
    private String transfer_status;
    private String principal;
    private String server_amount_rate;
    private String max_transfer_amt;
    private String min_transfer_amt;
    private String contract_num;
    private String transfer_order_id;
    private String buy_from_transfer;
    private String transfer_money;
    private String discount_money;
    private String left_days;
    private String goods_type;
    private String remain_tip;
    private String product_code;
    private String product_num;

    public String getProduct_num() {
        return product_num;
    }

    public void setProduct_num(String product_num) {
        this.product_num = product_num;
    }

    public String getProduct_code() {
        return product_code;
    }

    public void setProduct_code(String product_code) {
        this.product_code = product_code;
    }

    public String getC_time() {
        return c_time;
    }

    public void setC_time(String c_time) {
        this.c_time = c_time;
    }

    public String getContext() {
        return context;
    }

    public void setContext(String context) {
        this.context = context;
    }

    public String getMoney() {
        return money;
    }

    public void setMoney(String money) {
        this.money = money;
    }

    public int getSy_time() {
        return sy_time;
    }

    public void setSy_time(int sy_time) {
        this.sy_time = sy_time;
    }

    public String getTips() {
        return tips;
    }

    public void setTips(String tips) {
        this.tips = tips;
    }

    public String getOrderId() {
        return order_id;
    }

    public void setOrderId(String order_id) {
        this.order_id = order_id;
    }

    public String getInvestEarnings() {
        return invest_earnings;
    }

    public void setInvestEarnings(String invest_earnings) {
        this.invest_earnings = invest_earnings;
    }

    public String getInvestDays() {
        return invest_days;
    }

    public void setInvestDays(String invest_days) {
        this.invest_days = invest_days;
    }

    public String getInvestEndDate() {
        return invest_end_date;
    }

    public void setInvestEndDate(String invest_end_date) {
        this.invest_end_date = invest_end_date;
    }

    public String getInvestMoney() {
        return invest_money;
    }

    public void setInvestMoney(String invest_money) {
        this.invest_money = invest_money;
    }

    public String getTodayRate() {
        return today_rate;
    }

    public void setTodayRate(String today_rate) {
        this.today_rate = today_rate;
    }

    public String getProgress() {
        return progress;
    }

    public void setProgress(String progress) {
        this.progress = progress;
    }

    public String getStatus() {
        return status;
    }

    public void setStatus(String status) {
        this.status = status;
    }

    public String getAssertsId() {
        return asserts_id;
    }

    public void setAssertsId(String asserts_id) {
        this.asserts_id = asserts_id;
    }

    public String getProspectiveYieldAmt() {
        return prospective_yield_amt;
    }

    public void setProspectiveYieldAmt(String prospective_yield_amt) {
        this.prospective_yield_amt = prospective_yield_amt;
    }

    public String getUrl() {
        return url;
    }

    public void setUrl(String url) {
        this.url = url;
    }

    public String getTransferStatus() {
        return transfer_status;
    }

    public void setTransferStatus(String transfer_status) {
        this.transfer_status = transfer_status;
    }

    public String getPrincipal() {
        return principal;
    }

    public void setPrincipal(String principal) {
        this.principal = principal;
    }

    public String getServerAmountRate() {
        return server_amount_rate;
    }

    public void setServerAmountrate(String server_amount_rate) {
        this.server_amount_rate = server_amount_rate;
    }

    public String getMaxTransferAmt() {
        return max_transfer_amt;
    }

    public void setMaxTransferAmt(String max_transfer_amt) {
        this.max_transfer_amt = max_transfer_amt;
    }

    public String getMinTransferAmt() {
        return min_transfer_amt;
    }

    public void setMinTransferAmt(String min_transfer_amt) {
        this.min_transfer_amt = min_transfer_amt;
    }

    public String getContractNum() {
        return contract_num;
    }

    public void setContractNum(String contract_num) {
        this.contract_num = contract_num;
    }

    public String getTransferOrderId() {
        return transfer_order_id;
    }

    public void setTransferOrderId(String transfer_order_id) {
        this.transfer_order_id = transfer_order_id;
    }

    public String getBuyFromTransfer() {
        return buy_from_transfer;
    }

    public void setBuyFromTransfer(String buy_from_transfer) {
        this.buy_from_transfer = buy_from_transfer;
    }

    public String getTransferMoney() {
        return transfer_money;
    }

    public void setTransferMoney(String transfer_money) {
        this.transfer_money = transfer_money;
    }

    public String getDiscountMoney() {
        return discount_money;
    }

    public void setDiscountMoney(String discount_money) {
        this.discount_money = discount_money;
    }

    public String getLeftDays() {
        return left_days;
    }

    public void setLeftDays(String left_days) {
        this.left_days = left_days;
    }

    public String getOrder_id() {
        return order_id;
    }

    public void setOrder_id(String order_id) {
        this.order_id = order_id;
    }

    public String getGoods_type() {
        return goods_type;
    }

    public void setGoods_type(String goods_type) {
        this.goods_type = goods_type;
    }

    public String getRemain_tip() {
        return remain_tip;
    }

    public void setRemain_tip(String remain_tip) {
        this.remain_tip = remain_tip;
    }

    @Override
    public String toString() {
        return "RegularRecord{" +
                "c_time='" + c_time + '\'' +
                ", context='" + context + '\'' +
                ", money='" + money + '\'' +
                ", sy_time=" + sy_time +
                ", tips='" + tips + '\'' +
                ", order_id='" + order_id + '\'' +
                ", invest_earnings='" + invest_earnings + '\'' +
                ", invest_days='" + invest_days + '\'' +
                ", invest_end_date='" + invest_end_date + '\'' +
                ", invest_money='" + invest_money + '\'' +
                ", today_rate='" + today_rate + '\'' +
                ", progress='" + progress + '\'' +
                ", status='" + status + '\'' +
                ", asserts_id='" + asserts_id + '\'' +
                ", prospective_yield_amt='" + prospective_yield_amt + '\'' +
                ", url='" + url + '\'' +
                ", transfer_status='" + transfer_status + '\'' +
                ", principal='" + principal + '\'' +
                ", server_amount_rate='" + server_amount_rate + '\'' +
                ", max_transfer_amt='" + max_transfer_amt + '\'' +
                ", min_transfer_amt='" + min_transfer_amt + '\'' +
                ", contract_num='" + contract_num + '\'' +
                ", transfer_order_id='" + transfer_order_id + '\'' +
                ", buy_from_transfer='" + buy_from_transfer + '\'' +
                ", transfer_money='" + transfer_money + '\'' +
                ", discount_money='" + discount_money + '\'' +
                ", left_days='" + left_days + '\'' +
                ", goods_type='" + goods_type + '\'' +
                ", remain_tip='" + remain_tip + '\'' +
                ", product_code='" + product_code + '\'' +
                ", product_num='" + product_num + '\'' +
                '}';
    }
}
