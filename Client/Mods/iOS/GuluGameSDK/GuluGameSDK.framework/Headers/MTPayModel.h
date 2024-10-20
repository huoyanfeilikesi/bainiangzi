//
//  MTPayModel.h
//  OVERSEAS-SDK-DEMO
//
//  Created by  czk on 17/7/4.
//  Copyright © 2017年 czk. All rights reserved.
//

#import <Foundation/Foundation.h>

@interface MTPayModel : NSObject
@property (nonatomic, copy) NSString *amount; // 充值应付价格
@property (nonatomic, copy) NSString *ramount;// 充值实付价格
@property (nonatomic, copy) NSString *region;// 充值服务器（用于区分来自不同的服务器充值请求，自定义填写）
@property (nonatomic, copy) NSString *product_id;// 产品ID
@property (nonatomic, copy) NSString *product_name;// 产品名字
@property (nonatomic, copy) NSString *pname;// 充值面额名称
@property (nonatomic, copy) NSString *count;// 产品数量（例如：充值60G币，这里传入的是60）
@property (nonatomic, copy) NSString *out_trade_no;// 订单号
@property (nonatomic, copy) NSString *para;// 请求时传入的参数，原样返回
@property (nonatomic, copy) NSString *currency;// 货币单位
@end
