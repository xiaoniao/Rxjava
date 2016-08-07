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
				System.out.println("complete");
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
		// filter();
		// takeLast();
		// last();
		// lastFunc();
		// lastOrDefault();
		// takeLastBuffer();
		// skip();
		// skipLast();
		// take();
		// first();
		// takeFirst();
		// firstOrDefault();
		// elementAt();
		// elementAtDefault();
		// sample();
		// throttleLast();
		// throttleFirst();
		// throttleWithTimeout();
		// debounce();
		timeout();
		// distinct();
		// distinctFunc();
		// distinctUntillChanged();
		// ofType();
		// ignoreElements();
	}

	// filter
	public static void filter() {
		getObservable().filter(new Func1<String, Boolean>() {

			@Override
			public Boolean call(String string) {
				if (string.startsWith("J")) {
					return true;
				}
				return false;
			}
		}).subscribe(getSubscriber());
	}

	// takeLast
	public static void takeLast() {
		getObservable().takeLast(3).subscribe(getSubscriber());
	}

	// last
	public static void last() {
		getObservable().last().subscribe(getSubscriber());
	}
	
	// last func1
	public static void lastFunc() {
		getObservable().last(new Func1<String, Boolean>() {
			@Override
			public Boolean call(String t) {
				if (!t.startsWith("D")) {
					return true;
				}
				return false;
			}
		}).subscribe(getSubscriber());
	}

	// lastOrDefault
	public static void lastOrDefault() {
		getObservable().lastOrDefault("mary").subscribe(getSubscriber());
	}

	// takeLastBuffer 转成集合
	public static void takeLastBuffer() {
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
	}

	// skip
	public static void skip() {
		getObservable().skip(1).subscribe(getSubscriber());
	}

	// skipLast
	public static void skipLast() {
		getObservable().skipLast(2).subscribe(getSubscriber());
	}

	// take
	public static void take() {
		getObservable().take(3).subscribe(getSubscriber());
	}

	// first
	public static void first() {
		getObservable().first().subscribe(getSubscriber());
	}

	// takeFirst 有限制条件
	public static void takeFirst() {
		getObservable().takeFirst(new Func1<String, Boolean>() {
			@Override
			public Boolean call(String string) {
				if (string.equals("Jack")) {
					return true;
				}
				return false;
			}
		}).subscribe(getSubscriber());
	}

	// firstOrDefault
	public static void firstOrDefault() {
		getObservable().firstOrDefault("mary").subscribe(getSubscriber());
	}

	// elementAt
	public static void elementAt() {
		getObservable().elementAt(1).subscribe(getSubscriber());
	}

	// elementAtDefault
	public static void elementAtDefault() {
		getObservable().elementAtOrDefault(20, "mary").subscribe(getSubscriber());
	}

	// sample 采样
	public static void sample() {
		long time = System.currentTimeMillis();
		Observable.create(new OnSubscribe<Bean>() {

			@Override
			public void call(Subscriber<? super Bean> t) {
				// 比如在这里可以进行网络请求
				for (int i = 1; i <= 5; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("i=" + i);
					t.onNext(new Bean("jack" + i)); // 虽然这里调用了onNext但是不一定会执行因为设置了采样sample
				}
				t.onCompleted(); // 为什么注释这句话 就不显示最近的一次了
			}
		}).sample(2000, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Bean>() {

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
				System.out.println("sample:" + t.getName() + "  " + System.currentTimeMillis());
			}
		});
		System.out.println(System.currentTimeMillis() - time);
	}

	// throttleLast sample的别名
	public static void throttleLast() {
		Observable.create(new OnSubscribe<Bean>() {

			@Override
			public void call(Subscriber<? super Bean> t) {
				// 比如在这里可以进行网络请求
				for (int i = 1; i <= 5; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("i=" + i);
					t.onNext(new Bean("jack" + i));
				}
			}
		}).throttleLast(2000, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Bean>() {

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
	}

	// throttleFirst
	public static void throttleFirst() {
		Observable.create(new OnSubscribe<Bean>() {

			@Override
			public void call(Subscriber<? super Bean> t) {
				// 比如在这里可以进行网络请求
				for (int i = 1; i <= 5; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("i=" + i);
					t.onNext(new Bean("jack" + i));
				}
			}
		}).throttleFirst(2000, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Bean>() {

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
	}

	// throttleWithTimeout debounce的别名
	public static void throttleWithTimeout() {
		Observable.create(new OnSubscribe<Bean>() {

			@Override
			public void call(Subscriber<? super Bean> t) {
				// 比如在这里可以进行网络请求
				for (int i = 1; i <= 5; i++) {
					try {
						Thread.sleep(1000);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
					System.out.println("i=" + i);
					t.onNext(new Bean("jack" + i));
				}
			}
		}).throttleWithTimeout(2000, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Bean>() {

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
	}

	// debounce
	public static void debounce() {
		Observable.create(new OnSubscribe<Bean>() {

			@Override
			public void call(Subscriber<? super Bean> t) {
				// 比如在这里可以进行网络请求
//				for (int i = 1; i <= 5; i++) {
//					try {
//						Thread.sleep(1000);
//					} catch (InterruptedException e) {
//						e.printStackTrace();
//					}
//					System.out.println("i=" + i);
//					t.onNext(new Bean("jack" + i));
//				}
				for (int i = 0; i < 10; i++) {
		            int sleep = 100;
		            if (i % 3 == 0) {
		                sleep = 300;
		            }
		            try {
		                Thread.sleep(sleep);
		            } catch (InterruptedException e) {
		                e.printStackTrace();
		            }
		            t.onNext(new Bean("jack" + i));
		        }
		        t.onCompleted();
			}
		}).debounce(200, TimeUnit.MILLISECONDS).subscribe(new Subscriber<Bean>() {

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
	}

	// timeout 超时会执行error
	public static void timeout() {
		Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> t) {
				try {
					Thread.sleep(0);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				t.onNext("yes no time out");
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
	}

	// distinct
	public static void distinct() {
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
	}

	// distinct func1
	public static void distinctFunc() {
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
	}

	// distinctUntillChanged 去掉连续重复的值
	public static void distinctUntillChanged() {
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
	}

	// ofType
	public static void ofType() {
		Observable.just("string", 0).ofType(String.class).subscribe(new Subscriber<Object>() {

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
	}

	// ignoreElements
	public static void ignoreElements() {
		getObservable().ignoreElements().subscribe(getSubscriber());
	}

}
