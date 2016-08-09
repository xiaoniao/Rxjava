package com.liuzhuangzhuang.rx;

import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Observable.OnSubscribe;
import rx.Subscriber;
import rx.functions.Func1;
import rx.functions.Func2;

public class ErrorHandlingOperator {

	public static void main(String[] args) {

		// 数据源
		Observable<String> observable = Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> t) {
				t.onNext("a");
				t.onError(new Throwable("qixi's day"));
			}
		});

		// 遇到错误继续下一个Observable
		observable.onErrorResumeNext(Observable.just("2")).subscribe(new Subscriber<String>() {

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

		/**
		 * a <br>
		 * 2 <br>
		 * complete <br>
		 */

		observable.onErrorResumeNext(new Func1<Throwable, Observable<String>>() {

			@Override
			public Observable<String> call(Throwable t) {
				// System.out.println(t.getMessage());
				return Observable.just("2");
			}
		}).subscribe(new Subscriber<String>() {

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
		});

		// 遇到错误用 T 替换
		observable.onErrorReturn(new Func1<Throwable, String>() {

			@Override
			public String call(Throwable t) {
				return "2";
			}
		}).subscribe(new Subscriber<String>() {

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
		});

		// 遇到Exception时执行下一个Observable
		Observable.create(new OnSubscribe<String>() {

			@Override
			public void call(Subscriber<? super String> t) {
				t.onNext("a");
				int a = 1 / 0;
			}
		}).onExceptionResumeNext(Observable.just("2")).subscribe(new Subscriber<String>() {

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
		});

		// retry
		observable.retry(2).subscribe(new Subscriber<String>() {

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
		});

		observable.retry(new Func2<Integer, Throwable, Boolean>() {

			@Override
			public Boolean call(Integer t1, Throwable t2) {
				if (t1 < 3) {
					return true;
				}
				return false;
			}
		}).subscribe(new Subscriber<String>() {

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
		});

		System.out.println("--retrywhen");
		observable.retryWhen(new Func1<Observable<? extends Throwable>, Observable<?>>() {

			@Override
			public Observable<?> call(Observable<? extends Throwable> t) {
				return Observable.timer(5, TimeUnit.SECONDS);
			}
		}).subscribe(new Subscriber<String>() {

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
		});
	}
}
