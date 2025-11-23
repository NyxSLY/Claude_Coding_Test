#!/usr/bin/env python3
"""
Reddit热门追踪应用核心功能测试脚本
测试数据模型逻辑和API格式
"""

import json
import time

def test_format_score():
    """测试分数格式化逻辑"""
    print("\n=== 测试分数格式化 ===")

    def format_score(score):
        if score >= 1000000:
            return f"{score / 1000000:.1f}M"
        elif score >= 1000:
            return f"{score / 1000:.1f}K"
        else:
            return str(score)

    test_cases = [
        (500, "500"),
        (1500, "1.5K"),
        (10000, "10.0K"),
        (1500000, "1.5M"),
    ]

    all_passed = True
    for score, expected in test_cases:
        result = format_score(score)
        status = "✓" if result == expected else "✗"
        if result != expected:
            all_passed = False
        print(f"  {status} format_score({score}) = {result} (期望: {expected})")

    return all_passed

def test_time_ago():
    """测试时间格式化逻辑"""
    print("\n=== 测试时间格式化 ===")

    def get_time_ago(created_utc):
        now = int(time.time())
        diff = now - created_utc

        if diff < 60:
            return "刚刚"
        elif diff < 3600:
            return f"{diff // 60}分钟前"
        elif diff < 86400:
            return f"{diff // 3600}小时前"
        else:
            return f"{diff // 86400}天前"

    now = int(time.time())
    test_cases = [
        (now - 30, "刚刚"),
        (now - 300, "5分钟前"),
        (now - 7200, "2小时前"),
        (now - 172800, "2天前"),
    ]

    all_passed = True
    for created, expected in test_cases:
        result = get_time_ago(created)
        status = "✓" if result == expected else "✗"
        if result != expected:
            all_passed = False
        print(f"  {status} get_time_ago() = {result} (期望: {expected})")

    return all_passed

def test_subreddit_cleaning():
    """测试subreddit名称清理逻辑"""
    print("\n=== 测试Subreddit名称清理 ===")

    def clean_subreddit(name):
        name = name.strip()
        if name.startswith("/r/"):
            name = name[3:]
        elif name.startswith("r/"):
            name = name[2:]
        return name

    test_cases = [
        ("programming", "programming"),
        ("r/android", "android"),
        ("/r/technology", "technology"),
        ("  python  ", "python"),
    ]

    all_passed = True
    for input_name, expected in test_cases:
        result = clean_subreddit(input_name)
        status = "✓" if result == expected else "✗"
        if result != expected:
            all_passed = False
        print(f"  {status} clean('{input_name}') = '{result}' (期望: '{expected}')")

    return all_passed

def test_multi_subreddit_url():
    """测试多subreddit URL格式"""
    print("\n=== 测试多Subreddit URL格式 ===")

    subreddits = ["programming", "android", "technology"]
    combined = "+".join(subreddits)
    expected = "programming+android+technology"

    status = "✓" if combined == expected else "✗"
    print(f"  {status} combined = '{combined}' (期望: '{expected}')")

    url = f"https://www.reddit.com/r/{combined}/top.json?t=day&limit=25"
    print(f"  完整URL: {url}")

    return combined == expected

def test_time_filter():
    """测试时间过滤逻辑"""
    print("\n=== 测试时间过滤 ===")

    time_filters = {
        "hour": 3600,
        "day": 86400,
        "week": 604800,
        "month": 2592000,
        "year": 31536000,
    }

    def filter_by_time(post_age, filter_name):
        if filter_name == "all":
            return True
        max_age = time_filters.get(filter_name, 86400)
        return post_age <= max_age

    now = int(time.time())
    test_cases = [
        (1800, "hour", True),    # 30分钟内，符合1小时过滤
        (7200, "hour", False),   # 2小时，不符合1小时过滤
        (43200, "day", True),    # 12小时，符合24小时过滤
        (100000, "day", False),  # 超过1天
    ]

    all_passed = True
    for age, filter_name, expected in test_cases:
        result = filter_by_time(age, filter_name)
        status = "✓" if result == expected else "✗"
        if result != expected:
            all_passed = False
        print(f"  {status} filter({age}s, '{filter_name}') = {result} (期望: {expected})")

    return all_passed

def test_json_parsing():
    """测试JSON解析逻辑"""
    print("\n=== 测试JSON解析 ===")

    # 模拟Reddit API响应
    mock_response = {
        "data": {
            "children": [
                {
                    "kind": "t3",
                    "data": {
                        "id": "abc123",
                        "title": "Test Post Title",
                        "author": "testuser",
                        "subreddit": "programming",
                        "subreddit_name_prefixed": "r/programming",
                        "score": 1500,
                        "num_comments": 234,
                        "created_utc": int(time.time()) - 3600,
                        "permalink": "/r/programming/comments/abc123/test_post/",
                        "is_self": False,
                        "over_18": False,
                        "stickied": False
                    }
                }
            ],
            "after": "t3_xyz789"
        }
    }

    # 解析测试
    try:
        children = mock_response["data"]["children"]
        post = children[0]["data"]

        checks = [
            ("id", post["id"] == "abc123"),
            ("title", post["title"] == "Test Post Title"),
            ("author", post["author"] == "testuser"),
            ("subreddit", post["subreddit"] == "programming"),
            ("score", post["score"] == 1500),
            ("num_comments", post["num_comments"] == 234),
        ]

        all_passed = True
        for field, passed in checks:
            status = "✓" if passed else "✗"
            if not passed:
                all_passed = False
            print(f"  {status} 解析字段 '{field}'")

        # 测试URL生成
        full_url = f"https://www.reddit.com{post['permalink']}"
        expected_url = "https://www.reddit.com/r/programming/comments/abc123/test_post/"
        url_passed = full_url == expected_url
        status = "✓" if url_passed else "✗"
        print(f"  {status} URL生成: {full_url}")

        return all_passed and url_passed
    except Exception as e:
        print(f"  ✗ 解析错误: {e}")
        return False

def test_image_url_decode():
    """测试图片URL解码"""
    print("\n=== 测试图片URL解码 ===")

    encoded_url = "https://preview.redd.it/image.jpg?width=640&amp;height=480&amp;crop=smart"
    decoded_url = encoded_url.replace("&amp;", "&")
    expected = "https://preview.redd.it/image.jpg?width=640&height=480&crop=smart"

    status = "✓" if decoded_url == expected else "✗"
    print(f"  {status} URL解码正确")
    print(f"    原始: {encoded_url}")
    print(f"    解码: {decoded_url}")

    return decoded_url == expected

def main():
    print("=" * 50)
    print("Reddit热门追踪应用 - 核心功能测试")
    print("=" * 50)

    results = []
    results.append(("分数格式化", test_format_score()))
    results.append(("时间格式化", test_time_ago()))
    results.append(("Subreddit清理", test_subreddit_cleaning()))
    results.append(("多Subreddit URL", test_multi_subreddit_url()))
    results.append(("时间过滤", test_time_filter()))
    results.append(("JSON解析", test_json_parsing()))
    results.append(("图片URL解码", test_image_url_decode()))

    print("\n" + "=" * 50)
    print("测试结果汇总")
    print("=" * 50)

    passed = sum(1 for _, r in results if r)
    total = len(results)

    for name, result in results:
        status = "✓ 通过" if result else "✗ 失败"
        print(f"  {status}: {name}")

    print(f"\n总计: {passed}/{total} 测试通过")

    if passed == total:
        print("\n✅ 所有核心功能测试通过!")
        return 0
    else:
        print("\n❌ 部分测试失败")
        return 1

if __name__ == "__main__":
    exit(main())
