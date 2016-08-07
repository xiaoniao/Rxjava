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
		
		// zip
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
		Observable.create(
			// the source Observable		
			new OnSubscribe<String>() {
	
				@Override
				public void call(Subscriber<? super String> t) {
					try {
						System.out.println("sleep");
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					t.onNext("a");
					t.onNext("b");
					t.onCompleted();
				}
			})
		.join(
			// the second Observable to combine with the source Observable
			Observable.create(new OnSubscribe<Integer>() {
	
				@Override
				public void call(Subscriber<? super Integer> t) {
					for (int i = 1; i <= 5; i++) {
						try {
							Thread.sleep(1000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
						t.onNext(i);
					}
				}
			})
			, 
			// a function that accepts an item from the source Observable and 
			// returns an Observable whose lifespan governs the duration during which 
			// that item will combine with items from the second Observable
			new Func1<String, Observable<Long>>() {
	
				@Override
				public Observable<Long> call(String string) {
					System.out.println("1func : " + string);
					return Observable.timer(3, TimeUnit.SECONDS);
				}
			}, 
			// a function that accepts an item from the second Observable and 
			// returns an Observable whose lifespan governs the duration during which 
			// that item will combine with items from the first Observable
			new Func1<Integer, Observable<Long>>() {
	
				@Override
				public Observable<Long> call(Integer t) {
					System.out.println("2func : " + t);
					return Observable.timer(3, TimeUnit.SECONDS);
				}
			}, 
			// a function that accepts an item from the first Observable
			// and an item from the second Observable 
			// and returns an item to be emitted by the Observable returned from join
			new Func2<String, Integer, Observable<Bean>>() {
	
				@Override
				public Observable<Bean> call(String t1, Integer t2) {
					System.out.println("3func t1 : " + t1 + " t2 : " + t2);
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
				System.out.println("...");
			}
		});
//		try {
//			Thread.sleep(10000);
//		} catch (InterruptedException e1) {
//			e1.printStackTrace();
//		}
	}
}
