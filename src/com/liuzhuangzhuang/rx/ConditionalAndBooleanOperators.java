package com.liuzhuangzhuang.rx;

import java.util.ArrayList;
import java.util.List;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.Subscriber;

public class ConditionalAndBooleanOperators {

	private static Subscriber<String> getSubscriber() {
		return new Subscriber<String>() {

			@Override
			public void onCompleted() {
				System.out.println("complete");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("error" + e.getMessage());
			}

			@Override
			public void onNext(String t) {
				System.out.println(t);
			}
		};
	}

	public static void main(String[] args) {
		// amb();
		// defaultEmpty();
		// skipUntil();
		// skipWhile();
		// takeUntil();
		// takeWhile();
		// all();
		booleanOperators();
	}

	@SuppressWarnings("unchecked")
	public static void amb() {
		Observable<String> o1 = Observable.just("1", "a");
		Observable<String> o2 = Observable.just("2", "b");
		Observable<String> o3 = Observable.just("3", "c");
		Observable<String> o4 = Observable.just("4", "d");
		Observable<String> o5 = Observable.just("5", "e");
		Observable<String> o6 = Observable.just("6", "f");
		Observable<String> o7 = Observable.just("7", "g");
		Observable<String> o8 = Observable.just("8", "h");
		Observable<String> o9 = Observable.just("9", "i");

		Observable<?>[] array = new Observable<?>[] { o1, o2, o3, o4, o5, o6, o7, o8, o9 };
		List<Observable<String>> observables = new ArrayList<Observable<String>>();
		for (Observable<?> observable : array) {
			observables.add((Observable<String>) observable);
		}

		Observable.amb(observables).subscribe(getSubscriber());
		Observable.amb(o1, o2).subscribe(getSubscriber());
		Observable.amb(o1, o2, o3, o4, o5, o6, o7, o8, o9).subscribe(getSubscriber());
	}

	public static void defaultEmpty() {
		System.out.println("defalutEmpty1");
		Observable.just("1").defaultIfEmpty("defaultVaule").subscribe(getSubscriber());
		
		System.out.println("defalutEmpty2");
		Observable.empty().defaultIfEmpty("defaultValue").subscribe(new Subscriber<Object>() {

			@Override
			public void onCompleted() {
				System.out.println("complete");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("error");
			}

			@Override
			public void onNext(Object t) {
				System.out.println(t);
			}
		});
		
		System.out.println("defalutEmpty3");
		Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> t) {
				t.onCompleted();
			}
		}).defaultIfEmpty("defaultValue").subscribe(getSubscriber());
	}

	public static void skipUntil() {
		Observable.just("1").skipUntil(Observable.create(new OnSubscribe<String>() {
			
			@Override
			public void call(Subscriber<? super String> t) {
				t.onNext("a");
				try {
					Thread.sleep(4000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				System.out.println("second Observable over");
			}
		})).subscribe(getSubscriber());
	}

	// 等到返回 false ，才开始执行
	public static void skipWhile() {
		Observable.just("1", "2", "3").skipWhile(new Func1<String, Boolean>() {

			@Override
			public Boolean call(String t) {
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return false;
			}
		}).subscribe(getSubscriber());
	}

	public static void takeUntil() {
		// 先执行的Observable 然后执行Subscriber 最后执行func1 判断是否停止
		Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> t) {
				System.out.println("Observable 1 call");
				t.onNext("1");
				t.onNext("2");
				t.onNext("3");
				t.onCompleted();
			}
		}).takeUntil(new Func1<String, Boolean>() {

			@Override
			public Boolean call(String t) { // 返回true代表停止下来
				System.out.println("Func1 call " + t);
				if (t.equals("1")) {
					return false;
				} else if (t.equals("2")) {
					return true;
				} else if (t.equals("3")) {
					return false;
				} else if (t.equals("4")) {
					return true;
				}
				return false;
			}
		}).subscribe(getSubscriber());

		System.out.println("-----------------");
		// 先执行 Observable2 然后执行 Subscriber 最后执行 Observable1
		Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> t) {
				System.out.println("Observable 1 call");
				t.onNext("1");
				t.onNext("2");
				t.onNext("3");
				t.onCompleted();
			}
		}).takeUntil(Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> t) {
				System.out.println("Observable 2 call");
				// t.onNext("a");
			}
		})).subscribe(getSubscriber());
	}

	public static void takeWhile() {
		Observable.just("1", "2", "3").takeWhile(new Func1<String, Boolean>() {

			@Override
			public Boolean call(String t) {
				System.out.println("call " + t);
				if (t.equals("1")) {
					return true;
				} else if (t.equals("2")) {
					return false;
				} else if (t.equals("3")) {
					return true;
				}
				return true;
			}
		}).subscribe(getSubscriber());
	}

	public static void all() {
		Observable.just("1", "2", "3").all(new Func1<String, Boolean>() {
			@Override
			public Boolean call(String t) {
				// 1 2 3 条件都满足下面的订阅者才会返回true,否则返回false
				System.out.println("call " + t);
				return true;
			}
		}).subscribe(new Action1<Boolean>() {

			@Override
			public void call(Boolean t) {
				System.out.println(t);
			}
		});
	}

	// 布尔类型判断
	public static void booleanOperators() {
		Observable.just("1", "2").contains("1").subscribe(new Action1<Boolean>() {

			@Override
			public void call(Boolean t) {
				System.out.println(t);
			}
		});
		
		Observable.just("1", "2", "3", "4").exists(new Func1<String, Boolean>() {

			@Override
			public Boolean call(String t) {
				System.out.println("t : " + t);
				if (t.equals("3")) {
					return true;
				}
				return false;
			}
		}).subscribe(new Action1<Boolean>() {

			@Override
			public void call(Boolean t) {
				System.out.println(t);
			}
		});
		
		Observable.just("1").isEmpty().subscribe(new Action1<Boolean>() {

			@Override
			public void call(Boolean t) {
				System.out.println(t);
			}
		});
		
		Observable.sequenceEqual(Observable.just("1", "2"), Observable.just("1", "2")).subscribe(new Action1<Boolean>() {

			@Override
			public void call(Boolean t) {
				System.out.println(t);
			}
		});
		
		Observable.sequenceEqual(
				Observable.just("1", "2"),
				Observable.just("1", "3"),
				new Func2<String, String, Boolean>() {

					@Override
					public Boolean call(String t1, String t2) {
						System.out.println("t1 : " + t1 + " t2 : " + t2);
						if (t1.equals(t2)) {
							return true;
						}
						return false;
					}
				}
		).subscribe(new Action1<Boolean>() {

			@Override
			public void call(Boolean t) {
				System.out.println(t);
			}
		});
	}

	public static class TestCC<T> {

		private T t;

		public String getTclass(T t) {
			this.t = t;
			return this.t.getClass().getName();
		}

		public static <T> TestCC<T> getValue() {
			return new TestCC<T>();
		}
	}
}
