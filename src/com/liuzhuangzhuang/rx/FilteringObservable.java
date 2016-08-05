package com.liuzhuangzhuang.rx;

import java.util.List;
import java.util.concurrent.TimeUnit;

import com.liuzhuangzhuang.rx.CreateObservable.Bean;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;

public class FilteringObservable {

	private static Subscriber<String> getSubscriber() {
		return new Subscriber<String>() {

			@Override
			public void onCompleted() {
				 // System.out.println("complete");
			}

			@Override
			public void onError(Throwable t) {
				System.out.println("error" + t.getMessage());
			}

			@Override
			public void onNext(String string) {
				System.out.println(string);
			}
		};
	}

	private static Observable<String> getObservable() {
		return Observable.just("Jack", "Rose", "Jason Charles Bourne", "David Webb");
	}

	public static void main(String[] args) {

		// filter
		System.out.println("");
		Observable<String> observable = getObservable();
		observable.filter(new Func1<String, Boolean>() {

			@Override
			public Boolean call(String string) {
				if (string.startsWith("J")) {
					return true;
				}
				return false;
			}
		}).subscribe(getSubscriber());

		// takeLast
		System.out.println("");
		observable = getObservable();
		observable.takeLast(3).subscribe(getSubscriber());

		// last
		System.out.println("");
		observable.last().subscribe(getSubscriber());

		// lastOrDefault
		System.out.println("");
		observable.lastOrDefault("mary").subscribe(getSubscriber());

		// takeLastBuffer 转成集合
		System.out.println("");
		getObservable().takeLastBuffer(3).subscribe(new Subscriber<List<String>>() {

			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable arg0) {

			}

			@Override
			public void onNext(List<String> list) {
				for (String string : list) {
					System.out.println(string);
				}
			}
		});

		// skip
		System.out.println();
		getObservable().skip(1).subscribe(getSubscriber());

		// skipLast
		System.out.println();
		getObservable().skipLast(2).subscribe(getSubscriber());

		// take
		System.out.println();
		getObservable().take(3).subscribe(getSubscriber());

		// first
		System.out.println();
		getObservable().first().subscribe(getSubscriber());

		// takeFirst 附加了一个限制条件
		System.out.println();
		getObservable().takeFirst(new Func1<String, Boolean>() {
			@Override
			public Boolean call(String string) {
				if (string.equals("Jack")) {
					return true;
				}
				return false;
			}
		}).subscribe(getSubscriber());

		// firstOrDefault
		System.out.println();
		getObservable().firstOrDefault("mary").subscribe(getSubscriber());

		// elementAt
		System.out.println();
		getObservable().elementAt(1).subscribe(getSubscriber());

		// elementAtDefault
		getObservable().elementAtOrDefault(20, "mary").subscribe(getSubscriber());

		// sample
		System.out.println("--sample");
		Observable.create(new OnSubscribe<Bean>() {

			@Override
			public void call(Subscriber<? super Bean> t) {
				// 比如在这里可以进行网络请求
				for (int i = 0; i < 10; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("i=" + i);
					t.onNext(new Bean("jack" + i));
				}
			}
		}).sample(100, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Bean>() {

			@Override
			public void onCompleted() {
				
			}

			@Override
			public void onError(Throwable e) {
				
			}

			@Override
			public void onNext(Bean t) {
				System.out.println("sample:" + t.getName() + "  " + System.currentTimeMillis());
			}
		});

		// throttleLast
		System.out.println("--throttleLast");
		Observable.create(new OnSubscribe<Bean>() {

			@Override
			public void call(Subscriber<? super Bean> t) {
				// 比如在这里可以进行网络请求
				for (int i = 0; i < 10; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("i=" + i);
					t.onNext(new Bean("jack" + i));
				}
			}
		}).throttleLast(100, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Bean>() {

			@Override
			public void onCompleted() {
				
			}

			@Override
			public void onError(Throwable e) {
				
			}

			@Override
			public void onNext(Bean t) {
				System.out.println("throttleLast:" + t.getName() + "  " + System.currentTimeMillis());
			}
		});

		// throttleFirst
		System.out.println("--throttleFirst");
		Observable.create(new OnSubscribe<Bean>() {

			@Override
			public void call(Subscriber<? super Bean> t) {
				// 比如在这里可以进行网络请求
				for (int i = 0; i < 10; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("i=" + i);
					t.onNext(new Bean("jack" + i));
				}
			}
		}).throttleFirst(100, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Bean>() {

			@Override
			public void onCompleted() {
				
			}

			@Override
			public void onError(Throwable e) {
				
			}

			@Override
			public void onNext(Bean t) {
				System.out.println("throttleFirst:" + t.getName() + "  " + System.currentTimeMillis());
			}
		});

		// throttleWithTimeout
		System.out.println("--throttleWithTimeout");
		Observable.create(new OnSubscribe<Bean>() {

			@Override
			public void call(Subscriber<? super Bean> t) {
				// 比如在这里可以进行网络请求
				for (int i = 0; i < 10; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("i=" + i);
					t.onNext(new Bean("jack" + i));
				}
			}
		}).throttleWithTimeout(100, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Bean>() {

			@Override
			public void onCompleted() {
				
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("error:" + e.getMessage());
			}

			@Override
			public void onNext(Bean t) {
				System.out.println("throttleWithTimeout:" + t.getName() + "  " + System.currentTimeMillis());
			}
		});

		// debounce
		System.out.println("--debounce");
		Observable.create(new OnSubscribe<Bean>() {

			@Override
			public void call(Subscriber<? super Bean> t) {
				// 比如在这里可以进行网络请求
				for (int i = 0; i < 10; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("i=" + i);
					t.onNext(new Bean("jack" + i));
				}
			}
		}).debounce(100, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Bean>() {

			@Override
			public void onCompleted() {
				
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("error:" + e.getMessage());
			}

			@Override
			public void onNext(Bean t) {
				System.out.println("debounce:" + t.getName() + "  " + System.currentTimeMillis());
			}
		});

		// timeout
		Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> t) {
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				t.onNext("hello");
			}
		}).timeout(2, TimeUnit.SECONDS).subscribe(new Subscriber<String>() {

			@Override
			public void onCompleted() {
				System.out.println("complete");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("error:" + e.getMessage());
			}

			@Override
			public void onNext(String t) {
				System.out.println(t);
			}
		});

		// distinct
		System.out.println("--distinct");
		Observable.create(new OnSubscribe<Bean>() {

			@Override
			public void call(Subscriber<? super Bean> t) {
				t.onNext(new Bean("jack"));
				t.onNext(new Bean("jack"));

				Bean bean = new Bean("Bourne");
				t.onNext(bean);
				t.onNext(bean);
			}
		}).distinct().subscribe(new Subscriber<Bean>() {

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

		// distinct func
		System.out.println("--distinct func");
		Observable.create(new OnSubscribe<Bean>() {

			@Override
			public void call(Subscriber<? super Bean> t) {
				t.onNext(new Bean("jack"));
				t.onNext(new Bean("jack"));

				Bean bean = new Bean("Bourne");
				t.onNext(bean);
				t.onNext(bean);
			}

		}).distinct(new Func1<Bean, String>() {

			@Override
			public String call(Bean t) { // 这里返回的是键值
				return t.getName();
			}
		}).subscribe(new Subscriber<Bean>() {

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
				System.out.println("distinct onNext : " + t.getName());
			}
		});
		
		// distinctUntillChanged 去掉连续重复的值
		System.out.println("--distinctUntillChanged");
		Observable.create(new OnSubscribe<Bean>() {

			@Override
			public void call(Subscriber<? super Bean> t) {
				t.onNext(new Bean("jack"));
				t.onNext(new Bean("jack"));

				Bean bean = new Bean("Bourne");
				t.onNext(bean);
				t.onNext(bean);
			}

		}).distinctUntilChanged().subscribe(new Action1<Bean>() {

			@Override
			public void call(Bean t) {
				System.out.println(t.getName());
			}
			
		});
		
		// ofType
		System.out.println("--ofType");
		Observable.just("string", 0).ofType(String.class).subscribe(new Subscriber<Object> () {

			@Override
			public void onCompleted() {
				
			}

			@Override
			public void onError(Throwable e) {
				
			}

			@Override
			public void onNext(Object t) {
				System.out.println(t);
			}
		});
		
		// ignoreElements
		System.out.println("ignoreElements");
		getObservable().ignoreElements().subscribe(getSubscriber());
		
	}

}
