package com.ly.rxandroidsample;

import android.graphics.Bitmap;
import android.graphics.BitmapFactory;
import android.os.Environment;
import android.support.annotation.IntegerRes;
import android.support.v7.app.AppCompatActivity;
import android.os.Bundle;
import android.text.TextUtils;
import android.util.Log;
import android.widget.ImageView;

import java.io.File;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.concurrent.Future;
import java.util.concurrent.RunnableFuture;
import java.util.concurrent.TimeUnit;

import rx.Observable;
import rx.Scheduler;
import rx.Subscriber;
import rx.android.schedulers.AndroidSchedulers;
import rx.functions.Action1;
import rx.functions.Action2;
import rx.functions.Func1;
import rx.observables.ConnectableObservable;
import rx.schedulers.Schedulers;
import rx.schedulers.Timestamped;

public class MainActivity extends AppCompatActivity {

    public static final String TAG = "RxSample";
    ImageView mImgView;

    @Override
    protected void onCreate(Bundle savedInstanceState) {
        super.onCreate(savedInstanceState);
        setContentView(R.layout.activity_main);
        mImgView = (ImageView) findViewById(R.id.img_show);
//        rxNormal();

//        rxLazy();
//        rxFlow();
//        rxMapAndSchedules();
//        rxFlatMap();
//        rxConnect();
//        rxMerge();
//        rxTake();
//        rxTimer();
//        rxTimeStamp();
//        rxBackpressure();
        rxBackpressure2();
    }

    private void rxBackpressure2() {
        Observable observable = Observable.interval(1,TimeUnit.MILLISECONDS);
        observable.subscribeOn(Schedulers.newThread())
                .onBackpressureBuffer()
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Long>(){

                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onNext(Long aLong) {
                        try {
                            Thread.sleep(1000);
                        } catch (InterruptedException e) {
                            e.printStackTrace();
                        }
                        Log.d(TAG, "onNext: aLong = " + aLong);
                    }
                });
    }
    private void rxBackpressure() {
        Observable observable = Observable.interval(1,TimeUnit.MILLISECONDS);
        observable.subscribeOn(Schedulers.newThread())
                .buffer(1,TimeUnit.SECONDS)
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<List<Long>>() {
            @Override
            public void call(List<Long> integer) {
                try {
                    Thread.sleep(1000);
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
                Log.d(TAG, "call: " + integer.size());
            }
        });
    }

    private void rxTimeStamp() {
        Observable observable = Observable.from(new Integer[]{1, 3, 5, 2, 34, 7, 5, 86, 23, 43});
        observable.timestamp().subscribe(new Action1<Timestamped<Integer>>() {

            @Override
            public void call(Timestamped<Integer> integerTimestamped) {
                Log.d(TAG, "call: " + integerTimestamped.getTimestampMillis());
                Log.d(TAG, "call: " + integerTimestamped.getValue());

                SimpleDateFormat sdf = new SimpleDateFormat("yyyy-MM-dd-HH:mm:ss");
                String date = sdf.format(new Date(integerTimestamped.getTimestampMillis()));
                Log.d(TAG, "call: " + date);
            }
        });
    }

    private void rxTimer() {
        Observable obsevable = Observable.interval(1, TimeUnit.SECONDS);
        obsevable.subscribeOn(Schedulers.newThread())
                .observeOn(Schedulers.newThread())
                .subscribe(new Action1<Long>() {
                    @Override
                    public void call(Long aLong) {
                        Log.d(TAG, "call: aLong = " + aLong);
                    }
                });
    }

    private void rxTake() {
        Observable<Integer> observable = Observable.from(new Integer[]{1, 2, 3, 4, 5, 6, 7, 8, 9, 10});
        observable.subscribeOn(Schedulers.newThread())
                .take(4)
                .takeLast(2)
                .doOnNext(new Action1<Integer>() {
                    @Override
                    public void call(Integer integer) {
                        Log.d(TAG, "call: " + integer);
                    }
                })
                .observeOn(Schedulers.computation())
                .subscribe(new Subscriber<Integer>() {
                    @Override
                    public void onCompleted() {

                    }

                    @Override
                    public void onError(Throwable e) {

                    }

                    @Override
                    public void onNext(Integer integer) {
                        Log.d(TAG, "onNext: " + integer);
                    }
                });
    }

    private void rxMerge() {
        Observable<String> os1 = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                try {
                    Thread.sleep(2000);
                    subscriber.onNext(Thread.currentThread().getName() + "sleep 2 s");
                    subscriber.onCompleted();
                } catch (InterruptedException e) {
                    e.printStackTrace();
                }
            }
        }).subscribeOn(Schedulers.newThread());

        Observable<String> os2 = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onNext(Thread.currentThread().getName() + "run immediate");
                subscriber.onCompleted();
            }
        }).subscribeOn(Schedulers.newThread());

        Observable.merge(os1, os2).observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<String>() {
                    @Override
                    public void call(String s) {
                        Log.d(TAG, "call: s = " + s);
                    }
                });


    }

    private void rxConnect() {
        ConnectableObservable connObservable = Observable.from(new String[]{"one", "two", "three"}).publish();
        Action1<String> A = new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(TAG, "call: s = " + s);
            }
        };

        Action1<String> B = new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(TAG, "call: s = " + s);
            }
        };

        connObservable.subscribe(A);
        connObservable.subscribe(B);
        connObservable.connect();
    }

    private void rxFlatMap() {
        Observable.from(getClasses())
                .subscribeOn(Schedulers.newThread())
                .filter(new Func1<Classes, Boolean>() {
                    @Override
                    public Boolean call(Classes classes) {
                        Log.d(TAG, "filter call: ");
                        return classes != null;
                    }
                })
                .flatMap(new Func1<Classes, Observable<Student>>() {
                    @Override
                    public Observable<Student> call(Classes classes) {
                        Log.d(TAG, "flatMap call: ");
                        return Observable.from(classes.students);
                    }
                })
                .observeOn(Schedulers.newThread())
                .subscribe(new Subscriber<Student>() {
                    /**
                     * Notifies the Observer that the {@link Observable} has finished sending push-based notifications.
                     * <p>
                     * The {@link Observable} will not call this method if it calls {@link #onError}.
                     */
                    @Override
                    public void onCompleted() {

                    }

                    /**
                     * Notifies the Observer that the {@link Observable} has experienced an error condition.
                     * <p>
                     * If the {@link Observable} calls this method, it will not thereafter call {@link #onNext} or
                     * {@link #onCompleted}.
                     *
                     * @param e the exception encountered by the Observable
                     */
                    @Override
                    public void onError(Throwable e) {

                    }

                    /**
                     * Provides the Observer with a new item to observe.
                     * <p>
                     * The {@link Observable} may call this method 0 or more times.
                     * <p>
                     * The {@code Observable} will not call this method again after it calls either {@link #onCompleted} or
                     * {@link #onError}.
                     *
                     * @param student the item emitted by the Observable
                     */
                    @Override
                    public void onNext(Student student) {
                        Log.d(TAG, "onNext: Student = " + student.toString());
                    }
                });
    }

    private List<Classes> getClasses() {

        School school = new School();

        Classes c1 = new Classes("class one");

        Student stu1 = new Student("s1", "m", 10);
        Student stu2 = new Student("s2", "f", 20);
        Student stu3 = new Student("s3", "m", 40);
        Student stu4 = new Student("s4", "f", 20);
        Student stu5 = new Student("s5", "m", 40);

        c1.students.add(stu1);
        c1.students.add(stu2);
        c1.students.add(stu3);
        c1.students.add(stu4);
        c1.students.add(stu5);

        Classes c2 = new Classes("class two");

        Student stu11 = new Student("s11", "m", 10);
        Student stu21 = new Student("s21", "f", 20);
        Student stu31 = new Student("s31", "m", 40);
        Student stu41 = new Student("s41", "f", 20);
        Student stu51 = new Student("s51", "m", 40);

        c2.students.add(stu11);
        c2.students.add(stu21);
        c2.students.add(stu31);
        c2.students.add(stu41);
        c2.students.add(stu51);

        school.classes.add(c1);
        school.classes.add(c2);

        return school.classes;
    }

    private void rxMapAndSchedules() {

        Observable.just(Environment.getExternalStorageDirectory() + File.separator + "img-splash.jpg")

                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        if (TextUtils.isEmpty(s)) {
                            return false;
                        }
                        Log.d(TAG, Thread.currentThread().getName() + " call: file path  " + s);
                        return new File(s).exists();
                    }
                })
                .observeOn(Schedulers.io())
                .subscribeOn(Schedulers.newThread())
                .map(new Func1<String, Bitmap>() {
                    @Override
                    public Bitmap call(String s) {
                        Log.d(TAG, Thread.currentThread().getName() + " call: img path = " + s);
                        Bitmap bitmap = BitmapFactory.decodeFile(s);
                        return bitmap;
                    }
                })
                .observeOn(AndroidSchedulers.mainThread())
                .subscribe(new Action1<Bitmap>() {
                    @Override
                    public void call(Bitmap bitmap) {
                        Log.d(TAG, Thread.currentThread().getName() + " call: bitmap = " + bitmap);
                        mImgView.setImageBitmap(bitmap);
                    }
                });
    }

    private void rxFlow() {
        Observable.just("on", "off", "on", "off", "other", "there")
                .filter(new Func1<String, Boolean>() {
                    @Override
                    public Boolean call(String s) {
                        return s != null && (s.equals("on") || s.equals("off"));
                    }
                })
                .subscribe(new Subscriber<String>() {
                    @Override
                    public void onCompleted() {
                        Log.d(TAG, "onCompleted: ");
                    }

                    @Override
                    public void onError(Throwable e) {
                        Log.d(TAG, "onError: ");
                    }

                    @Override
                    public void onNext(String s) {
                        Log.d(TAG, "onNext: s = " + s);
                    }
                });
    }

    private void rxLazy() {

        Observable<String> observable = Observable.just("1", "2", "3", "4", "5", "6", "7", "8", "9", "10");

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: ");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
            }
        };

        observable.subscribe(subscriber);

        observable.subscribe(new Action1<String>() {
            @Override
            public void call(String s) {
                Log.d(TAG, "call: s = " + s);
            }
        });

        Action2 action2 = new Action2<String, String>() {
            @Override
            public void call(String s, String s2) {

            }
        };

    }

    private void rxNormal() {
        Observable<String> observable = Observable.create(new Observable.OnSubscribe<String>() {
            @Override
            public void call(Subscriber<? super String> subscriber) {
                subscriber.onStart();
                subscriber.onNext("1");
                subscriber.onNext("2");
                subscriber.onNext("3");
                subscriber.onNext("4");
                subscriber.onNext("5");
                subscriber.onNext("6");
                subscriber.onCompleted();
            }
        });

        Subscriber<String> subscriber = new Subscriber<String>() {
            @Override
            public void onCompleted() {
                Log.d(TAG, "onCompleted: ");
            }

            @Override
            public void onError(Throwable e) {
                Log.d(TAG, "onError: ");
            }

            @Override
            public void onNext(String s) {
                Log.d(TAG, "onNext: " + s);
            }
        };

        observable.subscribe(subscriber);
    }

    class Student {
        String name;
        String sex;
        int age;

        public Student(String name, String sex, int age) {
            this.name = name;
            this.sex = sex;
            this.age = age;
        }

        @Override
        public String toString() {
            return "name = " + name + ";" + sex + "age" + ";" + "age = " + age;
        }
    }

    class Classes {
        String name;
        List<Student> students = new ArrayList<>();

        public Classes(String name) {
            this.name = name;
        }
    }

    class School {
        List<Classes> classes = new ArrayList<>();
    }
}
