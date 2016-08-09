package com.liuzhuangzhuang.rx;

import java.util.List;

import com.liuzhuangzhuang.rx.CreateObservable.Bean;
import com.liuzhuangzhuang.rx.CreateObservable.Compare;

import rx.Observable;
import rx.Subscriber;
import rx.functions.Action1;
import rx.functions.Func1;
import rx.functions.Func2;
import rx.observables.GroupedObservable;

public class TransformingObservable {

	private static Subscriber<Bean> getSubscriber() {
		return new Subscriber<Bean>() {

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

	private static Subscriber<String> getSubscriberString() {
		return new Subscriber<String>() {

			@Override
			public void onCompleted() {
				System.out.println("complete");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("error : " + e.getMessage());
			}

			@Override
			public void onNext(String t) {
				System.out.println(t);
			}
		};
	}

	public static void main(String[] args) {
		// map();
		// flatMap();
		// scan();
		// groupBy();
		// buffer();
		// window();
		// cast();
	}

	// map
	public static void map() {
		// 把发射的数据转换成另一种数据类型
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
	}

	// flatMap
	public static void flatMap() {
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
		// Observable.just("1").flatMap(collectionSelector, resultSelector)
		// Observable.just("1").flatMap(onNext, onError, onCompleted)
		// Observable.just("1").flatMap(collectionSelector, resultSelector,
		// maxConcurrent)
		// Observable.just("1").flatMap(onNext, onError, onCompleted,
		// maxConcurrent)
	}

	// scan
	public static void scan() {
		// 扫描发射出来的每一个数据
		/**
		 * 1 left: 1 right:2 3 left: 3 right:3 6 left: 6 right:4 10 left: 10
		 * right:5 15
		 */
		Observable.just(1, 2, 3, 4, 5).scan(new Func2<Integer, Integer, Integer>() { // initialValue

			@Override
			public Integer call(Integer t1, Integer t2) {
				System.out.println("left: " + t1 + " right:" + t2);
				return t1 + t2;
			}
		}).subscribe(new Subscriber<Integer>() {

			@Override
			public void onCompleted() {
				System.out.println("complete");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("error : " + e.getMessage());
			}

			@Override
			public void onNext(Integer t) {
				System.out.println(t);
			}
		});
	}

	public static void groupBy() {
		// T k
		Observable.just(new Bean("jack"), new Bean("jack"), new Bean("rose"), new Bean("rose"))
				.groupBy(new Func1<Bean, String>() {

					@Override
					public String call(Bean t) {
						return t.getName(); // 按键分组 结果是Observable
					}
				}).subscribe(new Subscriber<GroupedObservable<String, Bean>>() {

					@Override
					public void onCompleted() {

					}

					@Override
					public void onError(Throwable e) {

					}

					@Override
					public void onNext(GroupedObservable<String, Bean> t) {
						System.out.println("key : " + t.getKey());
						t.subscribe(new Subscriber<Bean>() {

							@Override
							public void onCompleted() {

							}

							@Override
							public void onError(Throwable e) {

							}

							@Override
							public void onNext(Bean t) {
								System.out.println("\t\t" + t.getName());
							}
						});
					}
				});
	}

	// buffer
	public static void buffer() {
		// 转换成集合
		Observable.just("1", "2", "3", "4", "5", "6", "7").buffer(3).subscribe(new Action1<List<String>>() {

			@Override
			public void call(List<String> t) {
				System.out.print("next ");
				for (String string : t) {
					System.out.print(string + " ");
				}
				System.out.print("\n");
			}
		});
	}

	public static void window() {
		Observable.just("1", "2", "3", "4").window(3).subscribe(new Action1<Observable<String>>() {

			@Override
			public void call(Observable<String> t) {
				t.subscribe(getSubscriberString());
			}
		});
	}

	// cast 把发射出来的数据强制转换成另一个类型
	public static void cast() {
		// Bean 强转成 Compare
		Observable.just(new Bean("jack")).cast(Compare.class).subscribe(new Action1<Compare>() {

			@Override
			public void call(Compare t) {
				System.out.println("cast " + t.compare());
			}
		});
	}
}
