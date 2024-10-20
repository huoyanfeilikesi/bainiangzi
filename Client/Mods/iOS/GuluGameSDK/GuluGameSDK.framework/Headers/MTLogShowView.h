//
//  MTLogShowView.h
//  MorningtecSDKDemo
//
//  Created by fanxc on 2017/9/14.
//  Copyright © 2017年 czk. All rights reserved.
//

#import <UIKit/UIKit.h>

@interface MTLogShowView : UITextView
+ (void)addLog:(NSString *)text;

+ (void)showLog:(BOOL)isShow;
+ (void)clearLog;+ (void)showLog:(BOOL)isShow;
+ (void)clearLog;
@end
