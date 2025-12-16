## git 操作

每次提交代码前都要更新远程仓库到本地
```
git pull
```

更新功能时可在自己创建的分支更新
```
# 创建分支 dev, 如已有可不用创建
git branch dev

# 查看当前所在分支
git branch

# 切换到分支 dev
git switch dev

# 添加代码
git add .

# 提交代码
git commit -m "更新描述"
```

然后到 main 分支合并 dev 分支的代码
```
git switch main         # 切换到主分支
git merge dev           # 合并 dev 分支代码到当前 main 分支
```

最后更新代码到远程
```
git pull                # 拉取最新的远程代码到主分支，也就是 main 分支
git push origin main    # 推送到远程仓库
```
