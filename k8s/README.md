# Deploying

From an empty machine to the system running, with the steps in the order they have to
happen and the reason for each. Nothing here has been run against a live cluster yet —
there is no server. It is written to be applied and then corrected, not trusted.

## What you need

A machine with a public IP. One with 4 GB of RAM is enough for all nine pods; 2 GB is not,
because the five JVMs ask for 512 MB each before Postgres and RabbitMQ get any. A domain
pointed at it, if you want the certificate.

## 1 · k3s

```bash
curl -sfL https://get.k3s.io | sh -
```

That is the whole install. It brings Traefik as the ingress controller and a local-path
storage class, which is what the volume claims in `infra.yaml` bind to.

Copy `/etc/rancher/k3s/k3s.yaml` to your laptop as your kubeconfig and replace `127.0.0.1`
in it with the server's address.

## 2 · Access to the images

The images are published to GHCR and the packages are private, so the cluster needs a
token to pull them. Make a classic personal access token with `read:packages` and only
that:

```bash
kubectl create namespace adventistportal
kubectl -n adventistportal create secret docker-registry ghcr \
  --docker-server=ghcr.io --docker-username=Jeudry --docker-password=<token>
```

## 3 · The secrets

```bash
scripts/k8s-secrets.sh
```

It reads your local `.env` and writes a Kubernetes Secret. No value is stored in this
repository, and the script writes none to disk.

This is where "where do the secrets come from" ends up: the private signing key reaches
only the user pod, the mail password only the notification pod, and the API key only the
gateway. Read the `env` blocks in `services.yaml` — that split is the point, and it is
what makes a compromise of the smallest service smaller than a compromise of everything.

Kubernetes Secrets are base64, not encrypted, and anyone who can read them in the
namespace can read them all. That is the honest limit of this arrangement. The next step
up is Sealed Secrets or External Secrets, and it is worth taking the day it stops being
only you.

## 4 · Apply

```bash
kubectl apply -f k8s/namespace.yaml
kubectl apply -f k8s/config.yaml -f k8s/infra.yaml -f k8s/services.yaml
kubectl -n adventistportal get pods -w
```

Postgres runs `provision-roles.sql` the first time it starts on an empty volume, which
creates each service's schema and role. That happens once: creating a schema is
provisioning, and no service's changelog does it. If the volume already has data and the
roles are missing, apply the same file by hand with `scripts/db-provision-roles.sh`.

The five services then run their own Liquibase changelogs against their own schema, as
they do everywhere else.

## 5 · TLS

k3s has no certificate issuer. Install cert-manager and create a `letsencrypt`
ClusterIssuer; the Ingress in `services.yaml` already asks for it by name. Until then,
drop the `tls:` block and the annotation and it serves plain HTTP.

## Rolling out a new version

```bash
kubectl -n adventistportal set image deployment/user user=ghcr.io/jeudry/adventistportal/user:1.4.0
```

A tag pushed to the repository publishes `1.4.0` and `1.4` for all five services; `main`
publishes `edge`. Rolling back is the same command with the previous number, which is the
reason images are versioned at all.

`kubectl rollout undo deployment/user` also works and is faster to type when it matters.

## What this does not do yet

**Nothing applies it automatically.** There is a workflow that builds and publishes
images, and nothing that tells a cluster about them. That is deliberate until a cluster
exists: a deploy job pointed at nowhere is a step that looks done.

**One replica of everything.** The services are ready for more — the rate limiter keeps
its buckets in Redis, the outbox relay claims rows with `skip locked`, and sessions live
in the token rather than in memory. The exception is chat: it holds its WebSocket
connections in a map in the process, so a second replica would only reach the users
connected to it. That is the thing to fix before scaling chat, and it is why `replicas: 1`
is written rather than assumed.

**Postgres is in the cluster.** One node, one volume, no backups. It is the first thing to
move to something managed, and the only change is a connection string.
