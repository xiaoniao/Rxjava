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
		// just();
		// from();
		// repeat();
		// create();
		// defer();
		// range();
		// interval();
		// timer();
		// empty();
		// error();
		// never();
		
		// 防止有定时操作的时候程序退出，导致定时操作没了没了
		System.out.println("main sleep");
		try {
			Thread.sleep(10000);
		} catch (InterruptedException e1) {
			e1.printStackTrace();
		}
		System.out.println("main over");
	}
	
	// just 最多可以放10个参数 就是发送10次
	public static void just() {
		
		// 发射一个炮弹
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
				System.out.println("next");
				System.out.println(t.getName());
			}
		});

		// 发射10个炮弹
		observable = Observable.just(new Bean("jack1"), new Bean("jack2"), new Bean("jack3"), new Bean("jack4"), new Bean("jack5"),
				new Bean("jack6"), new Bean("jack7"), new Bean("jack8"),
				new Bean("jack9"), new Bean("jack10")); // 和 Observable.from(beans)等价的
		
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
				System.out.println("next");
				System.out.println(t.getName());
			}
		});

		// 发射一组兄弟弹
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
				System.out.println("next");
				// 因为调用的是 just(T) List<Bean>，是一个对象，所以只会执行一次 onNext , 而不同于上面传多个对象，执行多次 onNext
				for (Bean bean : t) {
					System.out.println(bean.getName());
				}
			}
		});
		
		// Object 类型
		Observable.just("1", 2, new Bean("3")).subscribe(new Subscriber<Object>() {
			
			@Override
			public void onCompleted() {
				System.out.println("complete");
			}
			
			@Override
			public void onError(Throwable throwable) {
				System.out.println("error");
			}
			
			@Override
			public void onNext(Object object) {
				if (object instanceof Bean) {
					Bean bean = (Bean) object;
					System.out.println(bean.getName());
				} else {
					System.out.println(object);
				}
			}
		});
	}
	
	// from
	public static void from() {
		// 数据
		List<Future<Bean>> futures = new ArrayList<Future<Bean>>(); // Future；［'fjʊtʃɚ］ 未来
		ExecutorService executor = Executors.newFixedThreadPool(10);
		for (int i = 0; i < 10; i++) {
			Future<Bean> future = executor.submit(new Callable<Bean>() {
				public Bean call() {
					return new Bean("jack-future");
				}
			});
			futures.add(future);
		}
		
		// List<Future<Bean>>
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
				System.out.println("next");
				try {
					System.out.println(t.get().getName());
				} catch (InterruptedException | ExecutionException e) {
					e.printStackTrace();
				}
			}
		});
		
		// Future<Bean>
		Observable<Bean> observable = Observable.from(executor.submit(new Callable<Bean>() {
			public Bean call() {
				return new Bean("jack-future-one");
			}
		})); 
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
				System.out.println("next");
				System.out.println(t.getName());
			}
		});

		// List<Bean>
		observable = Observable.from(beans); // 传的是数组，会执行多次 call
		observable.subscribe(new Action1<Bean>() {
			@Override
			public void call(Bean t) {
				System.out.println("action : " + t.getName());
			}
		});
	}
	
	// repeat
	public static void repeat() {
		Bean[] beans = new Bean[] { new Bean("jack") };
		
		// 在创建后发射数据前调用 （总共会发射两次，而不是2+1
		Observable.from(beans).repeat(2).subscribe(new Subscriber<Bean>() {

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

		// repeatWhen 指定时候后再重复执行一次
		Observable.from(beans).repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {
			
			@Override
			public rx.Observable<?> call(Observable<? extends Void> observable) {
				System.out.println("repeatWhen call");
				return Observable.timer(5, TimeUnit.SECONDS); // 5秒后再重新执行一次
			};
		}).subscribe(new Observer<Bean>() {
			
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
				System.out.println("repeatWhen : " + t.getName());
			}
		});
	}
	
	// create
	public static void create() {
		Observable<Bean> observable = Observable.create(new OnSubscribe<Bean>() {
			@Override
			public void call(Subscriber<? super Bean> t) {
				Bean bean = new Bean("rose1"); // 数据源
				t.onNext(bean);
				Bean bean2 = new Bean("rose2"); // 数据源
				t.onNext(bean2);
				t.onError(new Throwable("外星人入侵"));
				// t.onCompleted(); // 注释后，就不会调用 onCompleted 方法
				t.onNext(bean2); // 在 onError 或者 onCompleted 方法之后调用 onNext 不会报错，但也不会执行
				// just from 都是提供好数据然后自动执行的 onNext和onCompleted，但是 create 需要手动调用
			}
		});
		observable.subscribe(new Subscriber<Bean>() {
			
			@Override
			public void onCompleted() {
				System.out.println("complete");
			}
			
			@Override
			public void onError(Throwable e) {
				System.out.println("error : " + e.getMessage());
			}
			
			@Override
			public void onNext(Bean t) {
				System.out.println("create:" + t.getName());
			}
		});
	}
	
	// defer
	public static void defer() {
		Observable<Bean> observable = Observable.defer(new Func0<Observable<Bean>>() {
			@Override
			public Observable<Bean> call() {
				System.out.println("defer call");
				return Observable.just(new Bean("beanDef"));
			}
		});
		System.out.println("subscribe1");
		observable.subscribe(new Action1<Bean>() {
			@Override
			public void call(Bean t) {
				System.out.println(t.getName());
			}
		});
		System.out.println("subscribe2");
		observable.subscribe(new Action1<Bean>() { // 每订阅一次都会重新再走一遍流程
			@Override
			public void call(Bean t) {
				System.out.println(t.getName());
			}
		});
	}

	// range
	public static void range() {
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
	
	// interval
	public static void interval() {
		//  周期循环 这是一个异步操作不会堵住祝线程
		Observable.interval(1, TimeUnit.SECONDS).subscribe(new Observer<Long>() {
			
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

	// timer
	public static void timer() {
		// 3秒钟之后发射一个0L 这是一个异步操作 不会堵住线程，所以你要保证倒计时完成之后你的程序还在哦
		Observable.timer(3, TimeUnit.SECONDS).subscribe(new Subscriber<Long>() {
			
			@Override
			public void onCompleted() {
				System.out.println("complete");
			}
			
			@Override
			public void onError(Throwable e) {
				System.out.println("error : " + e.getMessage());
			}
			
			@Override
			public void onNext(Long t) {
				System.out.println(String.valueOf(t));
			}
		});
	}

	// empty
	public static void empty() {
		// 什么数据也不发射，然后执行 complete 方法
		Observable.empty().subscribe(new Observer<Object>() {
			
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

	// error
	public static void error() {
		// 什么数据也不发射，然后执行 error 方法
		Observable.error(new Throwable()).subscribe(new Observer<Object>() {
			
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

	// never
	public static void never() {
		// 什么数据也不发射，什么方法也不执行 什么情况下会用到这个特性呢
		Observable.never().subscribe(new Observer<Object>() {
			
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
