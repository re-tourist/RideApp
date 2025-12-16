# Git 工作流规范

> 文件路径：`docs/git-workflow.md`  
> 适用对象：本项目所有成员（包括代码开发 & 文档编写）  

---

## 简单使用方法

以下内容用于帮助项目成员快速理解并遵循本项目的 Git 使用规范。详细步骤可参考文档后续章节。

------

### 一、基本须知

- **必须理解分支（branch）的概念**
   GitHub 仓库的开发是通过多条分支协作完成的。不同分支承担不同作用，例如：
   `main`（线上稳定版）、`develop`（开发主干）、`feature/*`（功能开发）、`docs/*`（文档维护）、`hotfix/*`（紧急修复）等。

- **功能开发必须基于 develop 分支创建 feature 分支**
   所有功能开发都应按以下规范进行：

  1. **切换到 `develop` 分支并拉取最新代码**
  2. **基于 `develop` 创建自己的 `feature/<功能名>` 分支**
  3. 在 `feature` 分支上进行开发并提交代码
  4. 完成后将 `feature` 分支推送到远程仓库

  > ⚠️ 直接下载 ZIP 或从 `main` 分支 clone 都会导致流程错误，请务必基于 `develop` 开发。

- **功能开发完成后必须通过 PR 合并到 develop**
   单个功能（一个小需求）开发完成后，不允许直接合并。必须在 GitHub 发起 Pull Request（PR），由 Reviewer 审核通过后才能将 `feature` 分支合并回 `develop`。

- **Reviewer 的选择规则**
   PR 发起后，需要选择至少一名 Reviewer 进行审核。选择规则如下：

  **前端 PR：**

  - 项目负责人
  - 前端负责人 / 前端开发人员
  - 测试人员

  **后端 PR：**

  - 项目负责人
  - 后端负责人 / 后端开发人员
  - 测试人员

  **文档 PR（docs 分支）：**

  - 项目负责人
  - 文档负责人
  - 前端或后端负责人（任选其一）

  > 若 PR 发起者本身是部门负责人，则 Reviewer 应选择其他具备相关知识的开发人员，以确保互相审核。

- **文档分支（docs/\*）与紧急修复分支（hotfix/\*）**
   其使用方法基本与 `feature/*` 分支一致：

  - 基于正确的父分支创建
  - 开发 / 修改
  - 推送远程
  - 提 PR
  - 通过审核后合并

------

### 二、搭配 AI 工具辅助开发（推荐）

可以将本工作流文档提供给 AI（如 ChatGPT），让 AI 帮助你完成以下工作：

- 指导你应该从哪个分支拉出自己的工作分支
- 检查你的操作是否符合流程
- 为你生成符合 **Conventional Commits** 格式的 commit 信息
- 解释 Review 意见、辅助修改代码
- 协助理解 Git 冲突解决

善用 AI 可以显著降低 Git 学习成本，提高团队协作效率。

## 0. 分支总览（先有大地图）

我们项目使用的是一个「简化版 Git Flow」，有 6 类分支：

- `main`：线上 / 正式发布分支（**谁都不要在上面直接写代码**）
- `develop`：开发主干分支（**新的功能最终都会先合并到这里**）
- `feature/*`：功能开发分支（写代码主要在这里动手）
- `docs/*`：文档维护分支（写 / 改文档主要在这里）
- `release/*`：准备发版用的分支（测试 & 修 bug）
- `hotfix/*`：线上紧急修复分支（真的出大事故才用）

可以把它想象成一棵树：

```text
main         ←  正式版本，只有发布时才会合并到这里
  ↑
  └─ release/*   ←  发版前测试和修 bug，用完就删

develop      ←  日常开发主干，所有功能最终会到这里
  ↑   ↑
  │   └─ hotfix/*    ←  线上出 bug 时用的急救分支
  │
  ├─ feature/*  ←  开发新的功能（代码）
  └─ docs/*     ←  修改文档（说明、规范、报告等）
```

------

## 1. 仓库第一次使用：clone + 初始配置

### 1.1 克隆 develop 分支（推荐）

> **注意：** 不要只 clone 默认的 `main` 分支，我们开发都基于 `develop`。

```bash
# 1. clone 仓库（以 GitHub 为例）
git clone https://github.com/xxx/your-repo.git

# 2. 进入项目目录
cd your-repo

# 3. 切到 develop 分支
git checkout develop

# 4. 确保拉到最新代码
git pull origin develop
```

如果 `git checkout develop` 报错，说明本地没有这个分支，可以先 fetch 一下：

```bash
git fetch origin
git checkout develop
```

## 2. 日常开发：从 develop 拉出自己的分支

无论你是写代码还是写文档，你永远不要直接动这两个分支：

- `main`（线上正式版本）
- `develop`（整个团队共同使用的开发主干）

因为你直接改它们会导致：

- 别人拉最新代码会冲突
- 主干代码被破坏
- 发版时混进不稳定代码

所以我们统一 **每做一个任务，就创建自己的分支**。

### 🧩 2.1 分支类型与用途（必须记清）

功能开发 —— 用 `feature/...`
 文档编写 —— 用 `docs/...`

示例：

| 想做的事情          | 要创建的分支             |
| ------------------- | ------------------------ |
| 开发登录 API        | `feature/login-api`      |
| 开发骑行记录 UI     | `feature/ride-record-ui` |
| 更新 Git 工作流文档 | `docs/git-workflow`      |
| 修改项目 README     | `docs/readme-update`     |

**一个任务一条分支，不要多人共用同一个 feature 分支。**

------

### 🛠 2.2 从 develop 拉出自己的分支（完整命令步骤）

下面是一个绝对通用、任何项目都适用的步骤。
 即使你完全不懂 Git，只要照敲命令也可以完成。

------

#### 🔹 第 1 步：确保你当前在本地仓库中

进入项目文件夹，例如：

```bash
cd your-project-folder
```

------

#### 🔹 第 2 步：切换到 develop 分支

为什么要切换？
 因为我们的新分支必须从 `develop` 的最新代码拉出来。

```bash
git checkout develop
```

如果你看到：

```
Switched to branch 'develop'
```

说明 OK。

如果显示错误（大部分是 develop 不存在），先 fetch：

```bash
git fetch origin
git checkout develop
```

------

#### 🔹 第 3 步：把 develop 更新到最新版本（非常重要）

每次开始任何工作之前，都必须同步远程最新代码。

```bash
git pull origin develop
```

作用：

- 同步队友已经合并进来的代码
- 保证你创建的新分支不会落后别人太多
- 减少未来冲突

🚨 这是初学者最容易忘的一步。

------

#### 🔹 第 4 步：创建你的分支

根据你要做的事情选择 `feature/` 或 `docs/`。

##### 📌 示例 1：我要开发“路线规划 API”

```bash
git checkout -b feature/route-planning-api
```

##### 📌 示例 2：我要更新 Git 工作流文档

```bash
git checkout -b docs/git-workflow
```

创建成功会显示：

```
Switched to a new branch 'feature/route-planning-api'
```

------

#### 🔹 第 5 步：把你的分支推到远程

这一步让你的分支：

- 在 GitHub 可见
- 可以发 PR
- 可以被队友看见/帮你 review

命令：

```bash
git push -u origin feature/route-planning-api
# 或
git push -u origin docs/git-workflow
```

`-u` 的作用：

- 让本地分支绑定到远程分支
- 以后只需要 `git push` 就能自动推送（不用写分支名了）

------

#### 🎉 分支创建完毕，你就可以正式开始你的任务了！

分支创建完成后，你就可以：

- 写代码
- 改文档
- 提交 commit
- 推送到远程
- 最后用 PR 合并到 develop

------

### 🌟 小结：从 develop 拉自己分支的完整命令流程

这是一个你可以贴在桌面上的速查表：

```bash
# 1. 切换到 develop
git checkout develop

# 2. 拉取最新 develop
git pull origin develop

# 3. 创建自己的分支（示例：路线规划功能）
git checkout -b feature/route-planning-api

# 4. 推送到远程
git push -u origin feature/route-planning-api
```

如果是文档：

```bash
git checkout develop
git pull origin develop
git checkout -b docs/git-workflow
git push -u origin docs/git-workflow
```

------

## 3. 功能开发工作流（feature 分支）

### 3.1 创建 feature 分支

**场景**：要开发一个「骑行记录页面 UI」新功能。

**步骤：**

```bash
# 1. 确保当前在 develop，且是最新
git checkout develop
git pull origin develop

# 2. 创建自己的功能分支（命名尽量说明功能）
git checkout -b feature/ride-record-ui

# 3. 把新分支推到远程（方便别人看到 & 提 PR）
git push -u origin feature/ride-record-ui
```

命名建议（统一格式）：

- `feature/<模块>-<功能>`
  - 例：`feature/login-api`
  - 例：`feature/route-map`
  - 例：`feature/community-post`

### 3.2 在 feature 分支上开发 & 提交

1. 正常写代码（Android / 后端 / 前端）
2. 用 `git status` 看修改
3. 提交前尽量保证能在本地跑通

```bash
# 查看当前状态
git status

# 把需要提交的文件加入暂存区（可以用 . 也可以具体文件）
git add .

# 编写规范 commit 信息（建议英文或中英结合）
git commit -m "feat: 实现骑行记录页面基本 UI"
```

> 💡 提交小而清晰比一次提交一大坨好得多。

如果开发时间比较长，建议**经常提交**（一天至少 1~2 次），方便回滚和 review。

### 3.3 同步 develop 的最新改动（防止冲突）

如果其他同学在 `develop` 上合并了新功能，你的 `feature` 分支就会落后，需要更新：

```bash
# 仍在自己的 feature 分支上
git checkout feature/ride-record-ui

# 拉取远程最新的 develop
git fetch origin

# 把 develop 合并到当前分支
git merge origin/develop
# 过程中如果出现冲突，按提示在 IDE 里解决，然后：
git add <解决后的文件>
git commit
```

> 对初学者来说，用 `merge` 比 `rebase` 更容易理解。

### 3.4 功能完成后：发起 PR（Pull Request）

1. 在本地确认功能可用 & 基本自测通过
2. 把最新代码推到远程：

```bash
git push
# 或者 git push origin feature/ride-record-ui
```

1. 打开 GitHub / Gitee 页面：
   - 点击 **New pull request**
   - 选择：
     - **base**：`develop`
     - **compare**：`feature/ride-record-ui`
   - 标题建议：`feat: 完成骑行记录页面 UI`
   - 在描述中写：
     - 做了什么功能
     - 是否有数据库变更
     - 是否需要别人配置环境 / 注意事项
2. 指定 Reviewer（评审同学）

### 3.5 Review & 合并

- Reviewer 会在 PR 里：
  - 评论代码
  - 提出修改建议
- 你按建议修改后重新 `git commit` + `git push`，PR 会自动更新
- 通过后由：
  - **项目负责人或指定同学点击 Merge**，将 `feature` 合并进 `develop`

**合并完成后：可以删除分支**

```bash
# 本地删分支
git branch -d feature/ride-record-ui

# 远程删分支
git push origin --delete feature/ride-record-ui
```

> ✅ 结论：
>  **所有 `feature/\*` → `develop` 的合并，必须通过 PR，不允许直接 push 到 develop。**

------

## 4. 文档编写工作流（docs 分支）

> 文档也要走 Git 流程，只是我们用 `docs/*` 作为分支类型，逻辑上和 `feature/*` 一样，只是修改的主要是 `docs/` 或 `.md` 文件。

### 4.1 创建 docs 分支

**场景**：你要编写「Git 工作流规范」这个文档（也就是现在这个文件）。

```bash
# 1. 确保在最新的 develop
git checkout develop
git pull origin develop

# 2. 创建 docs 分支
git checkout -b docs/git-workflow

# 3. 推到远程
git push -u origin docs/git-workflow
```

### 4.2 修改 / 新增文档

例如，我们在 `docs/` 目录下创建这个文件：

```bash
# 在 IDE 或编辑器中创建 / 修改
docs/git-workflow.md
```

编辑完成后：

```bash
git status
git add docs/git-workflow.md
git commit -m "docs: 添加 Git 工作流规范文档"
git push
```

### 4.3 发起 PR：docs → develop

和功能分支完全一样：

- 在远程仓库上创建 PR：
  - base: `develop`
  - compare: `docs/git-workflow`
- 说明：
  - 文档用途
  - 主要内容
- 通过 review 后合并
- 删除 `docs` 分支（本地 + 远程）

> ✅ 文档修改也 **一律通过 PR 合并到 `develop`**，方便 review 文档内容是否准确、是否与代码一致。

------

## 5. 准备发版：release 分支工作流

> 当我们准备发布一个比较完整的版本（比如 v1.0.0），就会从 `develop` 拉一个 `release` 分支。

### 5.1 创建 release 分支

```bash
git checkout develop
git pull origin develop

git checkout -b release/v1.0.0
git push -u origin release/v1.0.0
```

从现在起：

- 不再往 `release/v1.0.0` 加新功能
- 只允许：
  - 修 bug
  - 改版本号、配置、文案等

### 5.2 在 release 上修 bug

有两种方式：

#### 简单学生版（推荐）

直接在 `release` 上修 bug：

```bash
git checkout release/v1.0.0

# 修改代码修 bug
git add .
git commit -m "fix: 修复登录页面崩溃问题"
git push
```

> 团队小、节奏不快时，可以接受，重点是 fix 都在 `release` 上记录清楚。

#### 更严格版（了解即可）

- 从 `release/v1.0.0` 拉 `bugfix/*` 分支
- 修完后 PR 回 `release/v1.0.0`

### 5.3 发布版本：release → main & 回合 develop

**步骤顺序：**

1. PR：`release/v1.0.0` → `main`

   - 通过后，代表正式版本要发布了

2. 在 `main` 上打 tag：

   ```bash
   git checkout main
   git pull origin main
   git tag -a v1.0.0 -m "Icyclist v1.0.0 初始正式版本"
   git push origin v1.0.0
   ```

3. 再 PR：`release/v1.0.0` → `develop`

   - 把发版过程中修的 bug 同步回开发主线

4. 删除 `release` 分支：

   ```bash
   git branch -d release/v1.0.0
   git push origin --delete release/v1.0.0
   ```

------

## 6. 线上紧急修复：hotfix 工作流

> 只有当「正式发布在 `main` 上的版本」出现严重问题（如：应用无法启动、数据严重错误）时，才使用 `hotfix` 分支。

### 6.1 创建 hotfix 分支

```bash
# 从 main 拉分支
git checkout main
git pull origin main

git checkout -b hotfix/login-crash
git push -u origin hotfix/login-crash
```

### 6.2 修复 bug

```bash
# 修代码
git add .
git commit -m "fix: 修复登录页因空指针导致的崩溃"

git push
```

### 6.3 合并到 main（修复线上）

1. 提 PR：`hotfix/login-crash` → `main`

2. 通过后，在 `main` 上打一个新 tag：

   ```bash
   git checkout main
   git pull origin main
   git tag -a v1.0.1 -m "Hotfix: 修复登录崩溃问题"
   git push origin v1.0.1
   ```

### 6.4 同步修复到 develop

接下来要把这个修复同步到开发主干，否则以后新版本会再次带上这个 bug。

有两种方式：

- 方式 A：PR：`hotfix/login-crash` → `develop`
- 方式 B：直接 `main` → `develop` 合并

推荐方式 A（逻辑更清晰）：

```bash
# 在远程建 PR：hotfix/login-crash → develop
# 合并后再删分支：
git branch -d hotfix/login-crash
git push origin --delete hotfix/login-crash
```

------

## 7. 必须记住的几条硬规则

1. **不要**在 `main` 上直接写代码或提交
2. **不要**在 `develop` 上直接开发功能
3. 所有功能开发在 `feature/*` 分支上完成
4. 所有文档修改在 `docs/*` 分支上完成
5. 合并到 `develop` / `main` 必须通过 PR：
   - `feature/*` → `develop`：必须 PR
   - `docs/*` → `develop`：必须 PR
   - `release/*` → `main`：必须 PR
   - `hotfix/*` → `main`：必须 PR
6. 发布版本时一定要打 tag（版本号统一用 `vX.Y.Z` 形式）
7. 分支用完及时删除，保持仓库整洁

------

## 8. 一个完整例子：从「写功能」到「发布版本」

> 你可以把下面这段当作一个「故事流程」记在脑子里：

1. PM 说：我们要做「骑行记录功能」
2. 你从 `develop` 拉分支：`feature/ride-record-api`
3. 开发 → commit → push → PR：`feature/ride-record-api → develop`
4. 过了几周，多个功能都做好了，需要发版本：
   - PM 确认要发 `v1.0.0`
   - 从 `develop` 建 `release/v1.0.0`
5. 测试在 `release/v1.0.0` 上测：
   - 发现 bug → 直接在 `release` 上修
6. 测试说 OK 了：
   - PR：`release/v1.0.0 → main`
   - 打 tag：`v1.0.0`
   - 再 PR：`release/v1.0.0 → develop`
7. 上线后发现登录崩溃：
   - 从 `main` 拉 `hotfix/login-crash`
   - 修完 → PR：`hotfix/login-crash → main`（打 tag `v1.0.1`）
   - 再 PR：`hotfix/login-crash → develop`
8. 期间你顺手改了一份文档：
   - 拉 `docs/git-workflow`
   - 写完文档 → PR：`docs/git-workflow → develop`

下面是一段可以**直接粘进 `docs/git-workflow.md`** 的章节内容，我帮你写成“规范文档口吻”，并且结合你们骑行项目给了很多具体示例。

------

## 9. Commit 信息规范：Conventional Commits（强制执行）

为确保仓库历史清晰、方便代码审查与后期回顾，本项目统一采用
 **Conventional Commits** 作为 commit 信息的书写规范。

### 9.1 基本格式

每一次提交必须符合以下格式之一：

```text
<type>: <简短说明>

<可选的详细描述>
```

或（带作用范围 scope）：

```text
<type>(scope): <简短说明>

<可选的详细描述>
```

- `type`：本次提交的类型（下面有固定列表）
- `scope`：本次修改影响的模块或范围（可选，但推荐使用）
- `简短说明`：一句话说明本次修改做了什么，**不需要句号**，不写“修复了 bug”，而是写“修复 xxx bug”

------

### 9.2 本项目约定的 type 列表

项目内统一使用以下几种 `type`，**其他类型如无特殊需要不建议新增**：

| type       | 用途说明                   | 示例                             |
| ---------- | -------------------------- | -------------------------------- |
| `feat`     | 新功能（Feature）          | 新增接口、新页面、新业务逻辑等   |
| `fix`      | 修复 Bug                   | 修复崩溃、修错别字、修逻辑错误   |
| `docs`     | 文档相关                   | 更新 README、设计文档、流程文档  |
| `refactor` | 代码重构（不改变外部功能） | 重构模块结构、优化代码可读性     |
| `style`    | 代码风格调整（无逻辑变化） | 调整空格、换行、命名、格式       |
| `test`     | 测试相关                   | 新增/修改单元测试、集成测试      |
| `chore`    | 杂项，不影响业务逻辑的变更 | 升级依赖、修改构建脚本、脚手架等 |
| `perf`     | 性能优化                   | 提升查询效率、减少耗时           |
| `build`    | 构建相关                   | Gradle 配置、打包脚本等          |
| `ci`       | CI/CD 配置相关             | GitHub Actions、流水线脚本修改   |

**最常用的 6 个：**`feat / fix / docs / refactor / style / chore`
 如果不知道用哪个，优先在这 6 个里选一个。

------

### 9.3 scope（作用范围）约定

`scope` 用于标记本次修改影响的模块，方便快速浏览提交历史。

结合本项目，建议使用以下 scope（不完全列举，可以根据需要扩展）：

- `backend`：后端整体
- `api`：通用接口逻辑
- `route`：路线规划相关
- `ride`：骑行记录相关
- `auth`：登录注册、权限相关
- `community`：社区/动态功能
- `user`：用户中心、个人信息
- `ui`：通用 UI 调整
- `docs`：文档（如果 type 已是 docs，可以省略）
- `config`：配置相关（yml、properties 等）

例如：

```text
feat(route): 新增根据骑行偏好推荐路线
fix(ride): 修复骑行结束后数据未保存的 bug
docs(api): 补充骑行记录上传接口说明
```

`scope` 不是强制必须填写，但**强烈建议填写**，特别是后端/多模块协作时。

------

### 9.4 常见提交场景示例（结合本项目）

下面的例子可以作为大家写提交信息时的参考。

#### 1）新增后端功能

```text
feat(backend): 初始化用户登录注册接口
feat(route): 新增根据起终点生成骑行路线的接口
feat(ride): 支持记录平均速度和海拔信息
```

#### 2）修复 Bug

```text
fix(auth): 修复 token 过期后未正确重定向登录页
fix(ride): 修复骑行暂停后继续，时间统计异常的问题
fix(route): 修复部分城市无法返回路线结果的 bug
```

#### 3）文档更新（包括 git-workflow.md）

```text
docs: 新增项目 Git 工作流与分支说明
docs(api): 补充路线规划接口的参数说明和示例
docs: 更新 README，添加本地运行步骤
```

#### 4）代码重构（逻辑不变）

```text
refactor(route): 抽取路线评分算法到独立服务
refactor(backend): 精简控制器层，提取通用响应封装
refactor(ui): 重构骑行记录页面布局结构
```

#### 5）修改代码风格 / 格式（不改变逻辑）

```text
style(backend): 统一控制器命名和缩进风格
style(ride): 调整变量命名，提升可读性
```

#### 6）测试相关

```text
test(route): 为路线规划接口补充单元测试
test(backend): 增加用户注册接口的集成测试
```

#### 7）依赖、构建、脚手架等杂项

```text
chore: 升级 Spring Boot 至 3.3.0
chore: 更新项目依赖版本并整理 gradle 脚本
build: 调整打包脚本，支持生成 docker 镜像
ci: 新增 Github Actions，提交后自动跑单元测试
```

------

### 9.5 提交信息的详细描述（可选但推荐）

当修改内容较多或较复杂时，建议在标题下增加详细描述：

```text
feat(ride): 新增骑行结束报告页面

- 展示本次骑行的总里程、时长、平均速度
- 接入后端骑行记录接口，支持实时刷新
- 增加简单的成就徽章展示（骑行里程里程碑）
```

编写详细描述的好处：

- Reviewer 更容易理解你的改动
- 自己以后回顾时也能快速记起当时做了什么
- 便于后续生成 CHANGELOG / 版本说明

------

### 9.6 统一要求（所有成员必须遵守）

1. **所有 commit 都必须按 Conventional Commits 规范书写**
   - 至少包含 `type: 简短说明`
   - 推荐使用 `type(scope): 简短说明`
2. 禁止使用以下提交说明：
   - `update`
   - `fix bug`
   - `test`
   - `改了一点东西`
   - `调试`
3. 如果一次改动内容太多、无法用一句话概括，说明你的 commit 过大，
   - 请适当拆分为多个 commit，每个 commit 对应一个比较完整的小改动（例如“重构某模块 + 添加一个新接口”就应该拆成两个提交）
4. PR 合并时，建议使用 **Squash and merge**，并将 squash 后的提交说明也改成符合本规范的格式，例如：
   - `feat(backend): 完成 v1.0.3 后端基础功能`

------

> **规则一句话总结：**
>  每次 commit 都要让别人（包括未来的自己）一眼看懂：
>  👉 “这次改了哪个模块（scope）” + “属于什么类型（type）” + “大概做了什么”。