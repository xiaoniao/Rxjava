package com.liuzhuangzhuang.rx;

import java.util.ArrayList;
import java.util.List;
import java.util.concurrent.Callable;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;
import java.util.concurrent.Future;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Observer;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func0;
import rx.functions.Func1;

public class CreateObservable {

	private static final List<Bean> beans = new ArrayList<>();

	static {
		for (int i = 0; i < 10; i++) {
			Bean bean = new Bean("jack" + i);
			beans.add(bean);
		}
	}

	static class Bean {

		private String name;

		public Bean(String name) {
			this.name = name;
		}

		public String getName() {
			return name;
		}

		public void setName(String name) {
			this.name = name;
		}
	}
	
	public static void main(String[] args) {
		repeat();
	}
	
	public static void just() {
		// just 最多可以放10个参数
		Observable<Bean> observable = Observable.just(new Bean("jack1"));
		observable.subscribe(new Subscriber<Bean>() {
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
		});

		observable = Observable.just(new Bean("jack1"), new Bean("jack2"),
				new Bean("jack3"), new Bean("jack4"), new Bean("jack5"),
				new Bean("jack6"), new Bean("jack7"), new Bean("jack8"),
				new Bean("jack9"), new Bean("jack10"));
		observable.subscribe(new Subscriber<Bean>() {
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
		});

		Observable<List<Bean>> observableBeans = Observable.just(beans); // 和上面的返回值不一样
		observableBeans.subscribe(new Subscriber<List<Bean>>() {

			@Override
			public void onCompleted() {
				System.out.println("complete");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("error");
			}

			@Override
			public void onNext(List<Bean> t) {
				// 因为传的是 List<Bean>，是一个对象，所以只会执行一次 onNext , 而不同于上面传多个对象，执行多次 onNext
				for (Bean bean : t) {
					System.out.println(bean.getName());
				}
			}
		});
	}
	
	public static void from() {
		// from (Future未来)
		List<Future<Bean>> futures = new ArrayList<Future<Bean>>();
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			Future<Bean> future = executor.submit(new Callable<Bean>() {
				public Bean call() {
					return new Bean("jack-future");
				}
			});
			futures.add(future);
		}
		Observable<Bean> observable = Observable.from(futures.get(0)); // Future<Bean>
		observable.subscribe(new Observer<Bean>() {

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
		});

		Observable<Future<Bean>> furureBeanObservable = Observable.from(futures); // List<Future<Bean>> 和just不一样，just是传List执行一次，from传List执行List.size()次
		furureBeanObservable.subscribe(new Observer<Future<Bean>>() {

			@Override
			public void onCompleted() {
				System.out.println("complete");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("error");
			}

			@Override
			public void onNext(Future<Bean> t) {
				try {
					System.out.println(t.get().getName());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		});

		observable = Observable.from(beans); // 传的是数组，会执行多次 call
		observable.subscribe(new Action1<Bean>() {
			@Override
			public void call(Bean t) {
				System.out.println("action : " + t.getName());
			}
		});
	}
	
	public static void repeat() {
		// repeat
		Observable<Bean> observable = Observable.from(beans);
		observable = observable.repeat(2);
		observable.subscribe(new Subscriber<Bean>() {

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
				System.out.println("repeat : " + t.getName());
			}
		});

		observable = observable.repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
			
			@Override
			public rx.Observable<?> call(Observable<? extends Void> observable) {
				System.out.println("repeatWhen call");
				return Observable.timer(1, TimeUnit.SECONDS);
			};
		});
		observable.subscribe(new Observer<Bean>() {
			@Override
			public void onCompleted() {
				System.out.println("complete");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("error" + e.getMessage());
			}

			@Override
			public void onNext(Bean t) {
				System.out.println("repeatWhen:" + t.getName());
			}
		});
	}
	
	public static void create() {
		// create
		Observable<Bean> observable = Observable.create(new OnSubscribe<Bean>() {
			@Override
			public void call(Subscriber<? super Bean> t) {
				System.out.println("Observable create");
				Bean bean = new Bean("rose"); // 数据源
				t.onNext(bean);
				t.onCompleted();
				// just from 都是提供好数据然后自动 onNext onCompleted 的，create 需要手动调用
			}
		});
		observable.subscribe(new Subscriber<Bean>() {
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
				System.out.println("create:" + t.getName());
			}
		});
	}
	
	public static void defer() {
		// defer
		Observable<Bean> observable = Observable.defer(new Func0<Observable<Bean>>() {
			@Override
			public Observable<Bean> call() {
				System.out.println("defer call");
				return Observable.just(new Bean("beanDef"));
			}
		});
		observable.subscribe(new Action1<Bean>() {
			@Override
			public void call(Bean t) {
				System.out.println("def1:" + t.getName());
			}
		});
		observable.subscribe(new Action1<Bean>() {
			@Override
			public void call(Bean t) {
				System.out.println("def2:" + t.getName());
			}
		});
		
	}

	public static void range() {
		// range
		Observable.range(0, 10).subscribe(new Subscriber<Integer>() {
			@Override
			public void onCompleted() {
				
			}
			@Override
			public void onError(Throwable e) {
				
			}
			@Override
			public void onNext(Integer t) {
				System.out.println(t);
			}
		});
	}
	
	public static void interval() {
		// interval
		Observable.interval(10000, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
			@Override
			public void onCompleted() {
				
			}
			@Override
			public void onError(Throwable e) {
				
			}
			@Override
			public void onNext(Long t) {
				System.out.println("interval:" + t);
			}
		});
	}

	public static void timer() {
		// timer
		Observable<Long> observableLong = Observable.timer(1, TimeUnit.SECONDS);
		observableLong.subscribe(new Subscriber<Long>() {
			@Override
			public void onCompleted() {
				
			}
			@Override
			public void onError(Throwable e) {
				
			}
			@Override
			public void onNext(Long t) {
				System.out.println("timer : " + t);
			}
		});
	}

	public static void empty() {
		// empty
		Observable<Object> observableObject = Observable.empty();
		observableObject.subscribe(new Observer<Object>() {
			
			@Override
			public void onCompleted() {
				System.out.println("empty complete");
			}
			
			@Override
			public void onError(Throwable e) {
				System.out.println("empty error");
			}
			
			@Override
			public void onNext(Object t) {
				System.out.println("empty next");
			}
		});
	}

	public static void error() {
		// error
		Observable<Object> observableObject = Observable.error(new Throwable());
		observableObject.subscribe(new Observer<Object>() {
			
			@Override
			public void onCompleted() {
				System.out.println("error complete");
			}
			
			@Override
			public void onError(Throwable e) {
				System.out.println("error error");
			}
			
			@Override
			public void onNext(Object t) {
				System.out.println("error next");
			}
		});
	}

	public static void never() {
		// never
		Observable<Object> observableObject = Observable.never();
		observableObject.subscribe(new Observer<Object>() {
			
			@Override
			public void onCompleted() {
				System.out.println("never complete");
			}
			
			@Override
			public void onError(Throwable e) {
				System.out.println("never error");
			}
			
			@Override
			public void onNext(Object t) {
				System.out.println("never next");
			}
		});
	}
}
