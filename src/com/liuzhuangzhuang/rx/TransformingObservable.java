package com.liuzhuangzhuang.rx;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

import com.liuzhuangzhuang.rx.CreateObservable.Bean;

public class TransformingObservable {

	private static Subscriber<Bean> getSubscriber() {
		return new Subscriber<Bean> () {

			@Override
			public void onCompleted() {
				System.out.println("complete");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("error");
			}

			@Override
			public void onNext(Bean t) {
				System.out.println(t.getName());
			}
		};
	}
	
	public static void main(String[] args) {
		
		// map 一种数据类型转换成另一种数据类型
		Observable.just("1").map(new Func1<String, Bean>() {

			@Override
			public Bean call(String t) {
				return new Bean(t);
			}
		}).subscribe(new Action1<Bean>() {

			@Override
			public void call(Bean t) {
				System.out.println(t.getName());
			}
		});
		
		// flatMap
		Observable.just("1").flatMap(new Func1<String, Observable<Bean>>() {

			@Override
			public Observable<Bean> call(String t) {
				return Observable.just(new Bean(t));
			}
		}).subscribe(getSubscriber());
		
		// maxConcurrent 最大线程数
		Observable.just("1").flatMap(new Func1<String, Observable<Bean>>() {

			@Override
			public Observable<Bean> call(String t) {
				return Observable.just(new Bean(t));
			}
		}, 10).subscribe(getSubscriber());
		
		// coffee coffee coffee
//		Observable.just("1").flatMap(collectionSelector, resultSelector)
//		Observable.just("1").flatMap(onNext, onError, onCompleted)
//		Observable.just("1").flatMap(collectionSelector, resultSelector, maxConcurrent)
//		Observable.just("1").flatMap(onNext, onError, onCompleted, maxConcurrent)
	}
}
