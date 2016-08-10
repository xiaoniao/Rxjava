package com.liuzhuangzhuang.rx;

import java.io.IOException;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Func1;

public class Difficulty {

	public static void main(String[] args) throws InterruptedException {
		retryWhen();
	}

	public static void repeatWhen() {
		Observable.just("1").repeatWhen(new Func1<Observable<? extends Void>, Observable<?>>() {

			@Override
			public Observable<?> call(Observable<? extends Void> t) {
				return Observable.timer(1, TimeUnit.SECONDS);
			}
		}).subscribe(new Subscriber<String>() {

			@Override
			public void onCompleted() {

			}

			@Override
			public void onError(Throwable e) {

			}

			@Override
			public void onNext(String t) {

			}
		});
	}

	/**
	 * 阻塞型的
	 * 
	 * A <br>
	 * C <br>
	 * 1 <br>
	 * B <br>
	 * 1 <br>
	 * B <br>
	 */
	public static void retryWhen() {
		Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> t) {
				t.onNext("1");
				t.onError(new IOException());
			}

		}).retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {

			@Override
			public Observable<?> call(Observable<? extends Throwable> t) {
				System.out.println("A");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}

				// 这里是真正是实现，其他的都是架子
				// 本身就是链式回调
				// 都是回调
				Observable<?> result = t.flatMap(new Func1<Throwable, Observable<?>>() {

					@Override
					public Observable<?> call(Throwable t) {

						System.out.println("B");
						try {
							Thread.sleep(2000);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}

						if (t instanceof IOException) {
							return Observable.just(null);
						}

						return Observable.error(t);
					}
				});

				System.out.println("C");
				try {
					Thread.sleep(2000);
				} catch (InterruptedException e) {
					e.printStackTrace();
				}
				return result;
			}
		}).subscribe(new Subscriber<String>() {

			@Override
			public void onCompleted() {
				System.out.println("complete");
			}

			@Override
			public void onError(Throwable e) {
				System.out.println("error");
			}

			@Override
			public void onNext(String t) {
				System.out.println(t);
			}
		});
	}
}
