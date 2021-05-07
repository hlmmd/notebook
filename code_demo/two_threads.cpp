#include <condition_variable>
#include <iostream>
#include <mutex>
#include <thread>
using namespace std;

int N = 10;
// 两个线程交替打印1-N
int main()
{
    mutex m;
    condition_variable cv;
    int x = 0;

    std::thread t1([&x, &m, &cv]() {
        while (x < N)
        {
            unique_lock<mutex> _(m);
            cout << ++x << endl;
            cv.notify_one();
            cv.wait(_);
        }
    });

    std::thread t2([&x, &m, &cv]() {
        while (x < N)
        {
            std::unique_lock<mutex> _(m);
            cv.wait(_);
            cout << ++x << endl;
            cv.notify_one();
        }
    });

    t1.join();
    t2.join();
    return 0;
}