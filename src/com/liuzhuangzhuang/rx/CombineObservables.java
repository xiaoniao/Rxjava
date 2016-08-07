package com.liuzhuangzhuang.rx;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.functions.Func3;

import com.liuzhuangzhuang.rx.CreateObservable.Bean;

public class CombineObservables {

	private static Subscriber<String> getSubscriber() {
		return new Subscriber<String>() {

			@Override
			public void onCompleted() {
				System.out.println("complete");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("error " + e.getMessage());
			}

			@Override
			public void onNext(String t) {
				System.out.println(t);
			}
		};
	}
	
	public static void main(String[] args) {
		// startWith
		System.out.println("--startWith");
		Observable.just("1").startWith("2", "3", "4", "5", "6", "7", "8", "9", "10").startWith("11").subscribe(getSubscriber());
		Observable.just("1").startWith(Observable.just("2")).subscribe(getSubscriber());
		
		// merge
		System.out.println("--merge");
		// Observable.merge(Observable.just("1", "2", "3"), Observable.just("3")).subscribe(getSubscriber());
		Observable.merge(Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> t) {
				t.onError(new Throwable("merge a error Observable"));
			}
		}), Observable.just("1", "2", "3")).subscribe(getSubscriber());
	
		// mergeDelayError
		System.out.println("--mergeDealyError");
		Observable.mergeDelayError(Observable.just("1", "2", "3"), Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> t) {
				t.onError(new Throwable("merge a error Observable1"));
			}
		}),Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> t) {
				t.onError(new Throwable("merge a error Observable2"));
			}
		}), Observable.just("4", "5", "6")).subscribe(getSubscriber());
		
		// zip 不同数据类型合并在一起
		System.out.println("--zip");
		Observable.zip(Observable.just("a", "b", "c"), Observable.just(1, 2, 3, 4), new Func2<String, Integer, Bean>() {

			@Override
			public Bean call(String t1, Integer t2) {
				return new Bean(t1 + t2);
			}
		}).subscribe(new Subscriber<Bean>() {

			@Override
			public void onCompleted() {
				
			}

			@Override
			public void onError(Throwable e) {
				
			}

			@Override
			public void onNext(Bean t) {
				System.out.println(t.getName());
			}
		});
		
		// combineLatest
		System.out.println("--combineLatest");
		Observable.combineLatest(Observable.just("a", "b", "c"), Observable.just("-", "+", "="), Observable.just(1, 2, 3), new Func3<String, String, Integer, Bean>() {

			@Override
			public Bean call(String t1, String t2, Integer t3) {
				return new Bean(t1 + t2 + t3);
			}
		}).subscribe(new Subscriber<Bean>() {

			@Override
			public void onCompleted() {
				
			}

			@Override
			public void onError(Throwable e) {
				
			}

			@Override
			public void onNext(Bean t) {
				System.out.println(t.getName());
			}
		});
		
		// 跟时间有关系
		
		// join
		System.out.println("--join");
		Observable.create(new OnSubscribe<String>() {
	
			@Override
			public void call(Subscriber<? super String> t) {
				t.onNext("liu");
			}
		}).join(Observable.create(new OnSubscribe<String>() {
	
				@Override
				public void call(Subscriber<? super String> t) {
					for (int i = 1; i <= 5; i++) {
						t.onNext("zhuang" + i);
					}
				}
			}),new Func1<String, Observable<Long>>() {
	
				@Override
				public Observable<Long> call(String string) {
					return Observable.timer(1, TimeUnit.SECONDS);
				}
			},new Func1<String, Observable<Long>>() {
	
				@Override
				public Observable<Long> call(String t) {
					return Observable.timer(1, TimeUnit.SECONDS);
				}
			},new Func2<String, String, Observable<Bean>>() {
	
				@Override
				public Observable<Bean> call(String t1, String t2) {
					return Observable.just(new Bean(t1 + t2));
				}
			})
		.subscribe(new Subscriber<Observable<Bean>>() {

			@Override
			public void onCompleted() {
				
			}

			@Override
			public void onError(Throwable e) {
				
			}

			@Override
			public void onNext(Observable<Bean> t) {
				t.subscribe(new Subscriber<Bean>() {

					@Override
					public void onCompleted() {
						
					}

					@Override
					public void onError(Throwable e) {
						
					}

					@Override
					public void onNext(Bean t) {
						System.out.println(t.getName());
					}
				});
			}
		});
		System.out.println("join over");
		
		// groupjoin
		
		// switchOnNext
		Observable.switchOnNext(Observable.just(Observable.just("a"), Observable.just("b"))).subscribe(getSubscriber());
	}
}
